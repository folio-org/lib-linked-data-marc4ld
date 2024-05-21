package org.folio.marc4ld.service.marc2ld.field;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
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
    var existProp = mapperHelper.getProperties(resource);
    var properties = fieldRule.mergeProperties(dataField, controlFields, existProp);
    resource.setDoc(mapperHelper.getJsonNode(properties));
    return resource;
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
    edgeResource.setDoc(mapperHelper.getJsonNode(properties));
    edgeResource.setLabel(fieldRule.getLabel(properties));
    edgeResource.setId(hashService.hash(edgeResource));
    parentResource.addOutgoingEdge(new ResourceEdge(parentResource, edgeResource, fieldRule.getPredicate()));
    return edgeResource;
  }
}
