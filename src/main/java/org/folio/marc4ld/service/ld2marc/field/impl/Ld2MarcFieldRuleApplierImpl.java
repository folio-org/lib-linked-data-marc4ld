package org.folio.marc4ld.service.ld2marc.field.impl;

import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.service.condition.ConditionChecker;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.ld2marc.field.ControlFieldRuleApplier;
import org.folio.marc4ld.service.ld2marc.field.IndicatorRuleApplier;
import org.folio.marc4ld.service.ld2marc.field.Ld2MarcFieldRuleApplier;
import org.folio.marc4ld.service.ld2marc.field.SubFieldRuleApplier;
import org.folio.marc4ld.service.ld2marc.field.param.ControlFieldParameter;
import org.folio.marc4ld.service.ld2marc.field.param.SubFieldParameter;

public class Ld2MarcFieldRuleApplierImpl implements Ld2MarcFieldRuleApplier {

  private static final String CONTROL_FIELD_PREFIX = "00";
  private final IndicatorRuleApplier indProperty1;
  private final IndicatorRuleApplier indProperty2;
  private final String tag;
  private final String parent;
  private final Marc4LdRules.FieldRule fieldRule;
  private final Collection<ControlFieldRuleApplier> controlFieldRuleAppliers;
  private final Collection<SubFieldRuleApplier> subFieldRuleAppliers;
  private final ConditionChecker conditionChecker;

  public Ld2MarcFieldRuleApplierImpl(
    String tag,
    Marc4LdRules.FieldRule fieldRule,
    ConditionChecker conditionChecker,
    DictionaryProcessor dictionaryProcessor
  ) {
    this.tag = tag;
    this.fieldRule = fieldRule;
    this.conditionChecker = conditionChecker;
    this.controlFieldRuleAppliers = mapToControlFieldRules(dictionaryProcessor);
    this.subFieldRuleAppliers = mapToSubFieldRules();
    this.indProperty1 = mapToIndicator(
      Marc4LdRules.FieldRule::getInd1,
      Marc4LdRules.Marc2ldCondition::getInd1
    );
    this.indProperty2 = mapToIndicator(
      Marc4LdRules.FieldRule::getInd2,
      Marc4LdRules.Marc2ldCondition::getInd2
    );
    this.parent = Optional.ofNullable(fieldRule.getParent())
      .orElse(StringUtils.EMPTY);
  }

  @Override
  public boolean isSuitable(ResourceEdge edge) {
    return isSuitableTypes(edge)
      && isSuitablePredicate(edge)
      && isTypeLikeRuleParent(edge);
  }

  @Override
  public boolean isDataFieldCreatable(Resource resource) {
    return !StringUtils.startsWith(tag, CONTROL_FIELD_PREFIX)
      && CollectionUtils.isNotEmpty(subFieldRuleAppliers)
      && conditionChecker.isLd2MarcConditionSatisfied(fieldRule, resource);
  }

  @Override
  public char getInd1(Resource resource) {
    return indProperty1.map(resource.getDoc());
  }

  @Override
  public char getInd2(Resource resource) {
    return indProperty2.map(resource.getDoc());
  }

  @Override
  public Collection<SubFieldParameter> getSubFields(Resource resource) {
    return subFieldRuleAppliers.stream()
      .map(rule -> rule.map(resource.getDoc()))
      .flatMap(Collection::stream)
      .toList();
  }

  @Override
  public Collection<ControlFieldParameter> getControlFields(Resource resource) {
    return controlFieldRuleAppliers.stream()
      .map(rule -> rule.map(resource.getDoc()))
      .flatMap(Collection::stream)
      .toList();
  }

  @Override
  public String getTag() {
    return tag;
  }

  private Collection<ControlFieldRuleApplier> mapToControlFieldRules(DictionaryProcessor dictionaryProcessor) {
    var controlFields = Optional.ofNullable(fieldRule.getControlFields())
      .orElse(Collections.emptyMap());

    return controlFields.entrySet()
      .stream()
      .map(entry -> new ControlFieldRuleApplierImpl(entry.getKey(), entry.getValue(), dictionaryProcessor))
      .map(ControlFieldRuleApplier.class::cast)
      .toList();
  }

  private IndicatorRuleApplier mapToIndicator(
    Function<Marc4LdRules.FieldRule, String> propertyGetter,
    Function<Marc4LdRules.Marc2ldCondition, String> defaultGetter
  ) {
    return new IndicatorRuleApplierImpl(
      propertyGetter.apply(fieldRule),
      ofNullable(fieldRule.getMarc2ldCondition())
        .map(defaultGetter)
        .orElse(null)
    );
  }

  private Collection<SubFieldRuleApplier> mapToSubFieldRules() {
    var subFields = Optional.ofNullable(fieldRule.getSubfields())
      .orElse(Collections.emptyMap());
    return subFields.entrySet()
      .stream()
      .sorted(Map.Entry.comparingByKey())
      .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (oldValue, newValue) -> oldValue))
      .entrySet()
      .stream()
      .map(entry -> new SubFieldRuleApplierImpl(entry.getValue(), entry.getKey()))
      .map(SubFieldRuleApplier.class::cast)
      .toList();
  }

  private boolean isSuitablePredicate(ResourceEdge edge) {
    return Optional.of(edge)
      .map(ResourceEdge::getPredicate)
      .map(predicate -> predicate.name().equals(fieldRule.getPredicate()))
      .orElse(true);
  }

  private boolean isSuitableTypes(ResourceEdge edge) {
    return Objects.equals(fieldRule.getTypes(), edge.getTarget().getTypeNames());
  }

  private boolean isTypeLikeRuleParent(ResourceEdge resourceEdge) {
    if (parent.isEmpty()) {
      return true;
    }
    return Optional.of(resourceEdge)
      .map(ResourceEdge::getSource)
      .map(resource -> resource.getTypeNames().contains(parent))
      .orElse(true);
  }
}
