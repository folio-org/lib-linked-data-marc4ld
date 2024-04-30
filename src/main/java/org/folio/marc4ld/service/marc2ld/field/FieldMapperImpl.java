package org.folio.marc4ld.service.marc2ld.field;

import static java.lang.String.join;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.folio.ld.dictionary.PredicateDictionary.valueOf;
import static org.folio.marc4ld.util.Constants.DependencyInjection.MARC2LD_MAPPERS_MAP;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.dictionary.model.ResourceType;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.condition.ConditionChecker;
import org.folio.marc4ld.service.marc2ld.field.property.PropertyMapper;
import org.folio.marc4ld.service.marc2ld.mapper.Marc2ldMapper;
import org.folio.marc4ld.service.marc2ld.relation.RelationProvider;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class FieldMapperImpl implements FieldMapper {

  private final ConditionChecker conditionChecker;
  private final PropertyMapper propertyMapper;
  private final ObjectMapper objectMapper;
  private final RelationProvider relationProvider;
  private final Map<String, List<Marc2ldMapper>> marc2ldMappersMap;
  private final FingerprintHashService hashService;

  public FieldMapperImpl(ConditionChecker conditionChecker, PropertyMapper propertyMapper, ObjectMapper objectMapper,
                         RelationProvider relationProvider, @Qualifier(MARC2LD_MAPPERS_MAP)
                         Map<String, List<Marc2ldMapper>> marc2ldMappersMap, FingerprintHashService hashService) {
    this.conditionChecker = conditionChecker;
    this.propertyMapper = propertyMapper;
    this.objectMapper = objectMapper;
    this.relationProvider = relationProvider;
    this.marc2ldMappersMap = marc2ldMappersMap;
    this.hashService = hashService;
  }

  @Override
  public void handleField(Resource parent, DataField dataField, List<ControlField> controlFields,
                          Marc4BibframeRules.FieldRule fieldRule) {
    if (conditionChecker.isMarc2LdConditionSatisfied(fieldRule, dataField, controlFields)) {
      final var parentResource = computeParentIfAbsent(parent, fieldRule);
      Resource mappedResource;
      if (fieldRule.isAppend()) {
        mappedResource = ofNullable(selectResourceFromEdges(parent, fieldRule.getTypes())).map(
            r -> appendResource(r, dataField, controlFields, fieldRule))
          .orElseGet(() -> addNewEdge(parentResource, dataField, controlFields, fieldRule));
      } else {
        mappedResource = addNewEdge(parentResource, dataField, controlFields, fieldRule);
        addRelation(parentResource, mappedResource, dataField, fieldRule);
      }

      findMapper(dataField.getTag(), fieldRule)
        .or(() -> findMapper(controlFields, fieldRule))
        .ifPresent(m -> m.map(new MarcData(dataField, controlFields), mappedResource));

      ofNullable(fieldRule.getEdges()).ifPresent(
        sr -> sr.forEach(subResource -> handleField(mappedResource, dataField, controlFields, subResource)));
      mappedResource.setId(hashService.hash(mappedResource));
    }
  }

  private Optional<Marc2ldMapper> findMapper(List<ControlField> controlFields, Marc4BibframeRules.FieldRule fieldRule) {
    return controlFields.stream()
      .map(ControlField::getTag)
      .flatMap(tag -> findMapper(tag, fieldRule).stream())
      .findFirst();
  }

  private Optional<Marc2ldMapper> findMapper(String tag, Marc4BibframeRules.FieldRule fieldRule) {
    if (isEmpty(tag) || isNull(fieldRule.getPredicate())) {
      return Optional.empty();
    }
    return marc2ldMappersMap.getOrDefault(tag, emptyList())
      .stream()
      .filter(mapper -> mapper.canMap(valueOf(fieldRule.getPredicate())))
      .findFirst();
  }

  private Resource computeParentIfAbsent(Resource parent, Marc4BibframeRules.FieldRule fieldRule) {
    var parentTypes = parent.getTypes().stream().map(ResourceType::getUri).collect(Collectors.toSet());
    if (fieldRule.getTypes().containsAll(parentTypes)) {
      return parent;
    }

    return selectResourceFromEdges(parent, ofNullable(fieldRule.getParent()).map(Set::of).orElse(Set.of()));
  }

  private Resource selectResourceFromEdges(Resource resource, Set<String> types) {
    if (resource.getTypeNames().containsAll(types)) {
      return resource;
    }
    return resource.getOutgoingEdges().stream().map(re -> selectResourceFromEdges(re.getTarget(), types))
      .filter(Objects::nonNull).findFirst().orElse(null);
  }

  private Resource appendResource(Resource resource, DataField dataField, List<ControlField> controlFields,
                                  Marc4BibframeRules.FieldRule fieldRule) {
    var properties = isNull(resource.getDoc()) ? new HashMap<String, List<String>>() :
      objectMapper.convertValue(resource.getDoc(), HashMap.class);
    propertyMapper.mapProperties(resource, dataField, fieldRule, controlFields, properties);
    return resource;
  }

  private Resource addNewEdge(Resource resource, DataField dataField, List<ControlField> controlFields,
                              Marc4BibframeRules.FieldRule fieldRule) {
    var edgeResource = new Resource();
    fieldRule.getTypes().stream().map(ResourceTypeDictionary::valueOf).forEach(edgeResource::addType);
    var properties = propertyMapper.mapProperties(edgeResource, dataField, fieldRule, controlFields, new HashMap<>());
    setLabel(edgeResource, properties, fieldRule.getLabel());
    resource.getOutgoingEdges().add(new ResourceEdge(resource, edgeResource, valueOf(fieldRule.getPredicate())));
    return edgeResource;
  }

  private void addRelation(Resource source, Resource target, DataField dataField,
                           Marc4BibframeRules.FieldRule fieldRule) {
    if (fieldRule.getRelation() != null) {
      relationProvider.findRelation(source, target, dataField, fieldRule)
        .ifPresent(resourceEdge -> source.getOutgoingEdges().add(resourceEdge));
    }
  }

  private void setLabel(Resource resource, Map<String, List<String>> properties, String labelProperty) {
    resource.setLabel(
      ofNullable(labelProperty)
        .flatMap(lp -> ofNullable(properties.get(PropertyDictionary.valueOf(lp).getValue())).map(vs -> join(SPACE, vs)))
        .orElseGet(() -> {
          log.warn("Setting random label to resource with types: {}, properties: {}, and labelProperty: {}",
            resource.getTypes(), properties, labelProperty);
          return UUID.randomUUID().toString();
        })
    );
  }

}
