package org.folio.marc4ld.service.marc2ld;

import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.SPACE;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.service.marc2ld.field.property.PropertyRule;
import org.folio.marc4ld.service.marc2ld.relation.Relation;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

@Builder
@AllArgsConstructor
public class Marc2LdFieldRuleApplierImpl implements Marc2ldFieldRuleApplier {

  private final String label;
  private final Relation relation;
  @NonNull
  private final Marc4BibframeRules.FieldRule fieldRule;
  @NonNull
  private final Collection<Marc2ldFieldRuleApplier> edgeRules;
  @NonNull
  private final PropertyRule propertyRule;
  @NonNull
  private final Collection<ResourceTypeDictionary> types;
  @NonNull
  private final PredicateDictionary predicate;

  @Override
  public Marc4BibframeRules.FieldRule getOriginal() {
    return fieldRule;
  }

  @Override
  public Resource computeParentIfAbsent(Resource resource) {
    if (isParent(resource)) {
      return resource;
    }
    var resourceTypes = Optional.ofNullable(fieldRule.getParent())
      .map(Set::of)
      .orElse(new HashSet<>());
    return selectResourceByTypes(resource, resourceTypes)
      .orElseGet(this::createResource);
  }

  @Override
  public Collection<Marc2ldFieldRuleApplier> getEdgeRules() {
    return edgeRules;
  }

  @Override
  public Optional<Resource> selectResourceFromEdges(Resource resource) {
    if (resource.getTypeNames().containsAll(fieldRule.getTypes())) {
      return Optional.of(resource);
    }
    return resource.getOutgoingEdges().stream()
      .map(re -> selectResourceFromEdges(re.getTarget()))
      .filter(Optional::isPresent)
      .flatMap(Optional::stream)
      .findFirst();
  }

  @Override
  public boolean isAppend() {
    return fieldRule.isAppend();
  }

  @Override
  public String getLabel(Map<String, List<String>> properties) {
    return Optional.ofNullable(label)
      .map(properties::get)
      .map(vs -> join(SPACE, vs))
      .orElseGet(UUID.randomUUID()::toString);
  }

  @Override
  public Optional<Relation> getRelation() {
    return Optional.ofNullable(relation);
  }

  @Override
  public Collection<Map<String, List<String>>> createProperties(DataField dataField,
                                                                Collection<ControlField> controlFields) {
    return propertyRule.create(dataField, controlFields);
  }

  @Override
  public Map<String, List<String>> mergeProperties(DataField dataField,
                                                   Collection<ControlField> controlFields,
                                                   Map<String, List<String>> values) {
    return propertyRule.merge(dataField, controlFields, values);
  }

  @Override
  public Collection<ResourceTypeDictionary> getTypes() {
    return types;
  }

  @Override
  public PredicateDictionary getPredicate() {
    return predicate;
  }

  private Resource createResource() {
    var res = new Resource();
    res.addType(ResourceTypeDictionary.valueOf(fieldRule.getParent()));
    return res;
  }

  private boolean isParent(Resource resource) {
    return types.containsAll(resource.getTypes());
  }

  private Optional<Resource> selectResourceByTypes(Resource resource, Set<String> resourceTypes) {
    if (resource.getTypeNames().containsAll(resourceTypes)) {
      return Optional.of(resource);
    }
    return resource.getOutgoingEdges()
      .stream()
      .map(re -> selectResourceByTypes(re.getTarget(), resourceTypes))
      .flatMap(Optional::stream)
      .findFirst();
  }
}
