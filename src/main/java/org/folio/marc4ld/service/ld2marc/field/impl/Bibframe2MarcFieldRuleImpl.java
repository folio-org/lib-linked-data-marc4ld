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
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.service.condition.ConditionChecker;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.ld2marc.field.Bibframe2MarcFieldRule;
import org.folio.marc4ld.service.ld2marc.field.ControlFieldRule;
import org.folio.marc4ld.service.ld2marc.field.IndicatorRule;
import org.folio.marc4ld.service.ld2marc.field.SubFieldRule;
import org.folio.marc4ld.service.ld2marc.field.param.ControlFieldParameter;
import org.folio.marc4ld.service.ld2marc.field.param.SubFieldParameter;

public class Bibframe2MarcFieldRuleImpl implements Bibframe2MarcFieldRule {

  private static final String CONTROL_FIELD_PREFIX = "00";
  private final IndicatorRule indProperty1;
  private final IndicatorRule indProperty2;
  private final String tag;
  private final String parent;
  private final Marc4BibframeRules.FieldRule fieldRule;
  private final Collection<ControlFieldRule> controlFieldRules;
  private final Collection<SubFieldRule> subFieldRules;
  private final ConditionChecker conditionChecker;

  public Bibframe2MarcFieldRuleImpl(
    String tag,
    Marc4BibframeRules.FieldRule fieldRule,
    ConditionChecker conditionChecker,
    DictionaryProcessor dictionaryProcessor
  ) {
    this.tag = tag;
    this.fieldRule = fieldRule;
    this.conditionChecker = conditionChecker;
    this.controlFieldRules = mapToControlFieldRules(dictionaryProcessor);
    this.subFieldRules = mapToSubFieldRules();
    this.indProperty1 = mapToIndicator(
      Marc4BibframeRules.FieldRule::getInd1,
      Marc4BibframeRules.Marc2ldCondition::getInd1
    );
    this.indProperty2 = mapToIndicator(
      Marc4BibframeRules.FieldRule::getInd2,
      Marc4BibframeRules.Marc2ldCondition::getInd2
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
      && CollectionUtils.isNotEmpty(subFieldRules)
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
    return subFieldRules.stream()
      .map(rule -> rule.map(resource.getDoc()))
      .flatMap(Collection::stream)
      .toList();
  }

  @Override
  public Collection<ControlFieldParameter> getControlFields(Resource resource) {
    return controlFieldRules.stream()
      .map(rule -> rule.map(resource.getDoc()))
      .flatMap(Collection::stream)
      .toList();
  }

  @Override
  public String getTag() {
    return tag;
  }

  private Collection<ControlFieldRule> mapToControlFieldRules(DictionaryProcessor dictionaryProcessor) {
    var controlFields = Optional.ofNullable(fieldRule.getControlFields())
      .orElse(Collections.emptyMap());

    return controlFields.entrySet()
      .stream()
      .map(entry -> new ControlFieldRuleImpl(entry.getKey(), entry.getValue(), dictionaryProcessor))
      .map(ControlFieldRule.class::cast)
      .toList();
  }

  private IndicatorRule mapToIndicator(
    Function<Marc4BibframeRules.FieldRule, String> propertyGetter,
    Function<Marc4BibframeRules.Marc2ldCondition, String> defaultGetter
  ) {
    return new IndicatorRuleImpl(
      propertyGetter.apply(fieldRule),
      ofNullable(fieldRule.getMarc2ldCondition())
        .map(defaultGetter)
        .orElse(null)
    );
  }

  private Collection<SubFieldRule> mapToSubFieldRules() {
    var subFields = Optional.ofNullable(fieldRule.getSubfields())
      .orElse(Collections.emptyMap());
    return subFields.entrySet()
      .stream()
      .sorted(Map.Entry.comparingByKey())
      .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (oldValue, newValue) -> oldValue))
      .entrySet()
      .stream()
      .map(entry -> new SubFieldRuleImpl(entry.getValue(), entry.getKey()))
      .map(SubFieldRule.class::cast)
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
