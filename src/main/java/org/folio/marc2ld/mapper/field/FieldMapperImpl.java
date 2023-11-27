package org.folio.marc2ld.mapper.field;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.folio.ld.dictionary.PredicateDictionary.valueOf;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.marc2ld.util.BibframeUtil.hash;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.mapper.condition.ConditionChecker;
import org.folio.marc2ld.mapper.field.property.PropertyMapper;
import org.folio.marc2ld.model.Resource;
import org.folio.marc2ld.model.ResourceEdge;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FieldMapperImpl implements FieldMapper {

  private final ConditionChecker conditionChecker;
  private final PropertyMapper propertyMapper;
  private final ObjectMapper objectMapper;

  @Override
  public void handleField(Resource parent, DataField dataField, Marc2BibframeRules.FieldRule fieldRule) {
    if (conditionChecker.isConditionSatisfied(fieldRule, dataField)) {
      final var parentResource = computeParentIfAbsent(parent, fieldRule);
      Resource mappedResource;
      if (fieldRule.isAppend()) {
        mappedResource = ofNullable(selectResourceFromEdges(parent, fieldRule.getTypes())).map(
            r -> appendResource(r, dataField, fieldRule))
          .orElseGet(() -> addNewEdge(parentResource, dataField, fieldRule));
      } else {
        mappedResource = addNewEdge(parentResource, dataField, fieldRule);
      }
      ofNullable(fieldRule.getEdges()).ifPresent(
        sr -> sr.forEach(subResource -> handleField(mappedResource, dataField, subResource)));
    }
  }

  private Resource computeParentIfAbsent(Resource parent, Marc2BibframeRules.FieldRule fieldRule) {
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

  private Resource appendResource(Resource resource, DataField dataField, Marc2BibframeRules.FieldRule fieldRule) {
    var properties = isNull(resource.getDoc()) ? new HashMap<String, List<String>>() :
      objectMapper.convertValue(resource.getDoc(), HashMap.class);
    propertyMapper.mapProperties(resource, dataField, fieldRule, properties);
    return resource;
  }

  private Resource addNewEdge(Resource resource, DataField dataField, Marc2BibframeRules.FieldRule fieldRule) {
    var edgeResource = new Resource();
    fieldRule.getTypes().stream().map(ResourceTypeDictionary::valueOf).forEach(edgeResource::addType);
    propertyMapper.mapProperties(edgeResource, dataField, fieldRule, new HashMap<>());
    getLabelSubfieldValues(fieldRule.getLabelFields(), dataField)
      .ifPresentOrElse(edgeResource::setLabel, () -> ofNullable(fieldRule.getConstants())
        .flatMap(c -> ofNullable(c.get(LABEL.name())))
        .ifPresentOrElse(edgeResource::setLabel, () -> edgeResource.setLabel(UUID.randomUUID().toString()))
      );
    edgeResource.setResourceHash(hash(edgeResource, objectMapper));
    resource.getOutgoingEdges().add(new ResourceEdge(resource, edgeResource, valueOf(fieldRule.getPredicate())));
    return edgeResource;
  }

  private Optional<String> getLabelSubfieldValues(List<Character> labelFields, DataField dataField) {
    return ofNullable(labelFields)
      .map(lfs -> lfs.stream()
        .map(dataField::getSubfield)
        .filter(Objects::nonNull)
        .map(Subfield::getData)
        .map(String::strip)
        .collect(Collectors.joining()))
      .filter(StringUtils::isNotEmpty);
  }

}
