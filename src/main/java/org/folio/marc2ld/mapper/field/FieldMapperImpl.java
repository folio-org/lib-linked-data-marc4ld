package org.folio.marc2ld.mapper.field;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.folio.ld.dictionary.PredicateDictionary.valueOf;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc2ld.util.BibframeUtil.hash;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.mapper.condition.ConditionChecker;
import org.folio.marc2ld.mapper.field.property.PropertyMapper;
import org.folio.marc2ld.model.Resource;
import org.folio.marc2ld.model.ResourceEdge;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FieldMapperImpl implements FieldMapper {

  private final ConditionChecker conditionChecker;
  private final PropertyMapper propertyMapper;
  private final ObjectMapper objectMapper;

  @Override
  public void handleField(Resource instance, DataField dataField, Marc2BibframeRules.FieldRule fieldRule) {
    if (conditionChecker.isConditionSatisfied(fieldRule, dataField)) {
      final var parentResource = computeParentIfAbsent(instance, fieldRule);
      if (fieldRule.isAppend()) {
        ofNullable(selectResourceFromEdges(instance, fieldRule.getTypes()))
          .ifPresentOrElse(r -> appendResource(r, dataField, fieldRule),
            () -> addNewEdge(parentResource, dataField, fieldRule));
      } else {
        addNewEdge(parentResource, dataField, fieldRule);
      }
    }
  }

  private Resource computeParentIfAbsent(Resource instance, Marc2BibframeRules.FieldRule fieldRule) {
    if (fieldRule.getTypes().contains(INSTANCE.name())) {
      return instance;
    }
    var parentResource = selectResourceFromEdges(instance, Set.of(fieldRule.getParent()));
    if (isNull(parentResource)) {
      parentResource = new Resource();
      parentResource.addType(ResourceTypeDictionary.valueOf(fieldRule.getParent()));
      parentResource.setLabel(UUID.randomUUID().toString());
      instance.getOutgoingEdges().add(new ResourceEdge(instance, parentResource,
        valueOf(fieldRule.getParentPredicate())));
      parentResource.setResourceHash(hash(parentResource, objectMapper));
    }
    return parentResource;
  }

  private Resource selectResourceFromEdges(Resource resource, Set<String> types) {
    if (resource.getTypes().stream().map(ResourceTypeDictionary::name).collect(Collectors.toSet()).equals(types)) {
      return resource;
    }
    return resource.getOutgoingEdges().stream()
      .map(re -> selectResourceFromEdges(re.getTarget(), types))
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(null);
  }

  private void appendResource(Resource resource, DataField dataField, Marc2BibframeRules.FieldRule fieldRule) {
    var properties = isNull(resource.getDoc()) ? new HashMap<String, List<String>>()
      : objectMapper.convertValue(resource.getDoc(), HashMap.class);
    propertyMapper.mapProperties(resource, dataField, fieldRule, properties);
  }

  private void addNewEdge(Resource resource, DataField dataField, Marc2BibframeRules.FieldRule fieldRule) {
    var edgeResource = new Resource();
    fieldRule.getTypes().stream().map(ResourceTypeDictionary::valueOf).forEach(edgeResource::addType);
    propertyMapper.mapProperties(edgeResource, dataField, fieldRule, new HashMap<>());
    ofNullable(fieldRule.getLabelField())
      .flatMap(lfr -> ofNullable(dataField.getSubfield(lfr)))
      .ifPresentOrElse(lf -> edgeResource.setLabel(lf.getData().strip()),
        () -> edgeResource.setLabel(UUID.randomUUID().toString()));
    edgeResource.setResourceHash(hash(edgeResource, objectMapper));
    resource.getOutgoingEdges().add(new ResourceEdge(resource, edgeResource,
      valueOf(fieldRule.getPredicate())));
  }

}
