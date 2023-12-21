package org.folio.marc4ld.service.marc2ld.field;

import static java.lang.String.join;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.folio.ld.dictionary.PredicateDictionary.valueOf;
import static org.folio.marc4ld.util.BibframeUtil.hash;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.model.Resource;
import org.folio.marc4ld.model.ResourceEdge;
import org.folio.marc4ld.service.condition.ConditionChecker;
import org.folio.marc4ld.service.marc2ld.field.property.PropertyMapper;
import org.folio.marc4ld.service.marc2ld.relation.RelationProvider;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FieldMapperImpl implements FieldMapper {

  private final ConditionChecker conditionChecker;
  private final PropertyMapper propertyMapper;
  private final ObjectMapper objectMapper;
  private final RelationProvider relationProvider;

  @Override
  public void handleField(Resource parent, DataField dataField, List<ControlField> controlFields,
                          Marc4BibframeRules.FieldRule fieldRule) {
    if (conditionChecker.isConditionSatisfied(fieldRule, dataField)) {
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
      ofNullable(fieldRule.getEdges()).ifPresent(
        sr -> sr.forEach(subResource -> handleField(mappedResource, dataField, controlFields, subResource)));
    }
  }

  private Resource computeParentIfAbsent(Resource parent, Marc4BibframeRules.FieldRule fieldRule) {
    if (fieldRule.getTypes().containsAll(parent.getTypes().stream().map(ResourceTypeDictionary::getUri).collect(
      Collectors.toSet()))) {
      return parent;
    }
    var parentResource =
      selectResourceFromEdges(parent, ofNullable(fieldRule.getParent()).map(Set::of).orElse(new HashSet<>()));
    if (isNull(parentResource)) {
      parentResource = new Resource();
      parentResource.addType(ResourceTypeDictionary.valueOf(fieldRule.getParent()));
      parentResource.setLabel(UUID.randomUUID().toString());
      parent.getOutgoingEdges()
        .add(new ResourceEdge(parent, parentResource, valueOf(fieldRule.getParentPredicate())));
      parentResource.setResourceHash(hash(parentResource, objectMapper));
    }
    return parentResource;
  }

  private Resource selectResourceFromEdges(Resource resource, Set<String> types) {
    if (resource.getTypes().stream().map(ResourceTypeDictionary::name).collect(Collectors.toSet()).containsAll(types)) {
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
    edgeResource.setResourceHash(hash(edgeResource, objectMapper));
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
        .orElseGet(() -> UUID.randomUUID().toString())
    );
  }

}
