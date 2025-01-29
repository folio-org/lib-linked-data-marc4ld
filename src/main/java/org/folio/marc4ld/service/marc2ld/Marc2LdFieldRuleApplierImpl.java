package org.folio.marc4ld.service.marc2ld;

import static java.util.Objects.isNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.service.marc2ld.field.property.PropertyRule;
import org.folio.marc4ld.service.marc2ld.relation.Relation;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

@Builder
@AllArgsConstructor
public class Marc2LdFieldRuleApplierImpl implements Marc2ldFieldRuleApplier {

  private final Relation relation;
  @NonNull
  private final Marc4LdRules.FieldRule fieldRule;
  @NonNull
  private final Collection<Marc2ldFieldRuleApplier> edgeRules;
  @NonNull
  private final PropertyRule propertyRule;
  @NonNull
  private final Collection<ResourceTypeDictionary> types;
  @NonNull
  private final PredicateDictionary predicate;

  @Override
  public Marc4LdRules.FieldRule getOriginal() {
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
      .orElseGet(this::createResourceByParent);
  }

  @Override
  public Collection<Marc2ldFieldRuleApplier> getEdgeRules() {
    return edgeRules;
  }

  @Override
  public Resource createResource() {
    var res = new Resource();
    Optional.of(fieldRule)
      .map(Marc4LdRules.FieldRule::getTypes)
      .stream()
      .flatMap(Collection::stream)
      .map(ResourceTypeDictionary::valueOf)
      .forEach(res::addType);
    return res;
  }

  @Override
  public Optional<Resource> selectResourceFromEdges(Resource resource) {
    if (resource.getTypeNames().containsAll(fieldRule.getTypes())) {
      return Optional.of(resource);
    }
    return resource.getOutgoingEdges().stream()
      .filter(re -> isNull(fieldRule.getPredicate()) || re.getPredicate().name().equals(fieldRule.getPredicate()))
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
  public Optional<Relation> getRelation() {
    return Optional.ofNullable(relation);
  }

  @Override
  public Collection<Map<String, List<String>>> createProperties(DataField dataField,
                                                                Collection<ControlField> controlFields) {
    return propertyRule.create(dataField, controlFields);
  }

  @Override
  public Collection<ResourceTypeDictionary> getTypes() {
    return types;
  }

  @Override
  public PredicateDictionary getPredicate() {
    return predicate;
  }

  private Resource createResourceByParent() {
    var res = new Resource();
    Optional.of(fieldRule)
      .map(Marc4LdRules.FieldRule::getParent)
      .map(ResourceTypeDictionary::valueOf)
      .ifPresent(res::addType);
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
