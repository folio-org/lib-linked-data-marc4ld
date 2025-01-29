package org.folio.marc4ld.service.marc2ld.field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.Marc2ldFieldRuleApplier;
import org.folio.marc4ld.service.marc2ld.mapper.mapper.MapperHelper;
import org.folio.marc4ld.service.marc2ld.relation.RelationProvider;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

@Builder
@RequiredArgsConstructor
public class FieldMapperImpl implements FieldMapper {

  private final FingerprintHashService hashService;
  private final RelationProvider relationProvider;
  private final MapperHelper mapperHelper;

  private final DataField dataField;
  private final Collection<ControlField> controlFields;
  private final Marc2ldFieldRuleApplier fieldRule;
  private final LabelService labelService;

  @Override
  public Collection<Resource> createResources() {
    return Optional.of(fieldRule)
      .map(Marc2ldFieldRuleApplier::createResource)
      .map(this::setProperties)
      .map(List::of)
      .orElseGet(Collections::emptyList);
  }

  @Override
  public Collection<Resource> createResources(Resource parent) {
    var parentResource = fieldRule.computeParentIfAbsent(parent);
    parentResource.setId(hashService.hash(parentResource));

    return Optional.of(fieldRule)
      .filter(Marc2ldFieldRuleApplier::isAppend)
      .flatMap(rule -> rule.selectResourceFromEdges(parent))
      .map(this::setProperties)
      .map(List::of)
      .orElseGet(() -> createNewEdges(parentResource));
  }

  private Resource setProperties(Resource resource) {
    var existingProps = mapperHelper.getProperties(resource);
    var newProps = fieldRule.createProperties(dataField, controlFields);
    var mergedPros = mergeProperties(existingProps, newProps);
    return resource.setDoc(mapperHelper.getJsonNode(mergedPros));
  }

  private Map<String, List<String>> mergeProperties(Map<String, List<String>> props1,
                                                    Collection<Map<String, List<String>>> props2) {
    Map<String, List<String>> mergedProps = new HashMap<>(props1);
    props2.forEach(
      prop -> prop.forEach(
        (key, value) -> mergedProps.merge(key, value, this::combineLists)
      )
    );
    return mergedProps;
  }

  private List<String> combineLists(List<String> list1, List<String> list2) {
    Set<String> combinedSet = new HashSet<>(list1);
    combinedSet.addAll(list2);
    return new ArrayList<>(combinedSet);
  }

  private List<Resource> createNewEdges(Resource resource) {
    var resources = fieldRule.createProperties(dataField, controlFields)
      .stream()
      .map(prop -> createResource(resource, prop))
      .toList();
    resources
      .forEach(res -> relationProvider.checkRelation(resource, res, dataField, fieldRule));
    return resources;
  }

  private Resource createResource(Resource parentResource, Map<String, List<String>> properties) {
    var edgeResource = new Resource();
    fieldRule.getTypes()
      .forEach(edgeResource::addType);
    labelService.setLabel(edgeResource, properties);
    edgeResource.setDoc(mapperHelper.getJsonNode(properties));
    edgeResource.setId(hashService.hash(edgeResource));
    parentResource.addOutgoingEdge(new ResourceEdge(parentResource, edgeResource, fieldRule.getPredicate()));
    return edgeResource;
  }
}
