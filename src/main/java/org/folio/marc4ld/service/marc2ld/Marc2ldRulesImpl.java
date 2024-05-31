package org.folio.marc4ld.service.marc2ld;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.field.property.Property;
import org.folio.marc4ld.service.marc2ld.field.property.PropertyRule;
import org.folio.marc4ld.service.marc2ld.field.property.PropertyRuleImpl;
import org.folio.marc4ld.service.marc2ld.field.property.builder.ControlFieldsPropertyBuilder;
import org.folio.marc4ld.service.marc2ld.field.property.builder.IndicatorPropertyBuilder;
import org.folio.marc4ld.service.marc2ld.field.property.builder.MappingPropertyBuilder;
import org.folio.marc4ld.service.marc2ld.field.property.builder.PropertyBuilder;
import org.folio.marc4ld.service.marc2ld.field.property.builder.SubfieldPropertyBuilder;
import org.folio.marc4ld.service.marc2ld.field.property.merger.PropertyMerger;
import org.folio.marc4ld.service.marc2ld.field.property.merger.PropertyMergerFactory;
import org.folio.marc4ld.service.marc2ld.field.property.transformer.PropertyTransformer;
import org.folio.marc4ld.service.marc2ld.field.property.transformer.PropertyTransformerFactory;
import org.folio.marc4ld.service.marc2ld.relation.Relation;
import org.folio.marc4ld.service.marc2ld.relation.RelationImpl;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Marc2ldRulesImpl implements Marc2ldRules {

  private final Map<String, Collection<Marc2ldFieldRuleApplier>> instanceRules;
  private final Map<String, Collection<Marc2ldFieldRuleApplier>> authorityRules;

  private final DictionaryProcessor dictionaryProcessor;
  private final PropertyTransformerFactory propertyTransformerFactory;
  private final PropertyMergerFactory propertyMergerFactory;

  @Autowired
  public Marc2ldRulesImpl(Marc4BibframeRules marc4BibframeRules,
                          DictionaryProcessor dictionaryProcessor,
                          PropertyTransformerFactory propertyTransformerFactory,
                          PropertyMergerFactory propertyMergerFactory) {
    this.dictionaryProcessor = dictionaryProcessor;
    this.propertyTransformerFactory = propertyTransformerFactory;
    this.propertyMergerFactory = propertyMergerFactory;

    this.instanceRules = initRules(marc4BibframeRules.getFieldRules());
    this.authorityRules = initRules(marc4BibframeRules.getAuthorityFieldRules());
  }

  @Override
  public Collection<Marc2ldFieldRuleApplier> findFiledRules(String tag) {
    return instanceRules.getOrDefault(tag, Collections.emptyList());
  }

  @Override
  public Collection<Marc2ldFieldRuleApplier> findAuthorityFiledRules(String tag) {
    return authorityRules.getOrDefault(tag, Collections.emptyList());
  }

  private Map<String, Collection<Marc2ldFieldRuleApplier>> initRules(
    Map<String, List<Marc4BibframeRules.FieldRule>> rules) {
    return rules
      .entrySet()
      .stream()
      .collect(Collectors.toMap(Map.Entry::getKey, entry -> createRules(entry.getValue())));
  }

  private Collection<Marc2ldFieldRuleApplier> createRules(List<Marc4BibframeRules.FieldRule> fieldRules) {
    return fieldRules.stream()
      .map(this::createRule)
      .toList();
  }

  private Marc2ldFieldRuleApplier createRule(Marc4BibframeRules.FieldRule rule) {
    var builder = Marc2LdFieldRuleApplierImpl.builder()
      .fieldRule(rule)
      .edgeRules(getEdges(rule))
      .propertyRule(getPropertyRule(rule))
      .types(getTypes(rule))
      .predicate(getPredicate(rule));
    getRelation(rule)
      .ifPresent(builder::relation);
    return builder.build();
  }

  private Optional<Relation> getRelation(Marc4BibframeRules.FieldRule rule) {
    return Optional.of(rule)
      .map(Marc4BibframeRules.FieldRule::getRelation)
      .map(fieldRelation -> new RelationImpl(fieldRelation.getCode(), fieldRelation.getText()));
  }

  private Collection<Marc2ldFieldRuleApplier> getEdges(Marc4BibframeRules.FieldRule rule) {
    return Optional.of(rule)
      .map(Marc4BibframeRules.FieldRule::getEdges)
      .orElseGet(Collections::emptyList)
      .stream()
      .map(this::createRule)
      .toList();
  }

  private Collection<ResourceTypeDictionary> getTypes(Marc4BibframeRules.FieldRule rule) {
    return rule.getTypes()
      .stream()
      .map(ResourceTypeDictionary::valueOf)
      .toList();
  }

  private PredicateDictionary getPredicate(Marc4BibframeRules.FieldRule rule) {
    return Optional.of(rule)
      .map(Marc4BibframeRules.FieldRule::getPredicate)
      .map(PredicateDictionary::valueOf)
      .orElse(PredicateDictionary.NULL);
  }

  private PropertyRule getPropertyRule(Marc4BibframeRules.FieldRule rule) {
    return PropertyRuleImpl.builder()
      .propertyTransformer(getTransformer(rule))
      .propertyMerger(getPropertyMerger(rule))
      .constantMerger(getConstantPropertyMerger(rule))
      .subFieldBuilders(getSubfieldBuilders(rule))
      .indicatorBuilders(getIndicatorBuilders(rule))
      .mappingsBuilders(getMappingsBuilders(rule))
      .controlFieldBuilders(getControlBuilders(rule))
      .constants(getConstants(rule))
      .build();
  }

  private PropertyTransformer getTransformer(Marc4BibframeRules.FieldRule rule) {
    return propertyTransformerFactory.get(rule);
  }

  private PropertyMerger getPropertyMerger(Marc4BibframeRules.FieldRule rule) {
    return propertyMergerFactory.get(rule);
  }

  private PropertyMerger getConstantPropertyMerger(Marc4BibframeRules.FieldRule rule) {
    return propertyMergerFactory.getConstant(rule);
  }

  private Collection<PropertyBuilder<DataField>> getSubfieldBuilders(Marc4BibframeRules.FieldRule rule) {
    if (Objects.isNull(rule.getSubfields())) {
      return Collections.emptyList();
    }
    return rule.getSubfields()
      .entrySet()
      .stream()
      .filter(entry -> Objects.nonNull(entry.getValue()))
      .map(entry -> new SubfieldPropertyBuilder(entry.getKey(), entry.getValue()))
      .collect(Collectors.toList());
  }

  private Collection<PropertyBuilder<DataField>> getIndicatorBuilders(Marc4BibframeRules.FieldRule rule) {
    if (ObjectUtils.allNull(rule.getInd1(), rule.getInd2())) {
      return Collections.emptyList();
    }
    return Collections.singleton(new IndicatorPropertyBuilder(rule));
  }

  private Collection<PropertyBuilder<DataField>> getMappingsBuilders(Marc4BibframeRules.FieldRule rule) {
    if (Objects.isNull(rule.getMappings())) {
      return Collections.emptyList();
    }
    return rule.getMappings()
      .entrySet()
      .stream()
      .map(entry -> new MappingPropertyBuilder(entry.getKey(), entry.getValue(), dictionaryProcessor))
      .collect(Collectors.toList());
  }

  private Collection<PropertyBuilder<Collection<ControlField>>> getControlBuilders(Marc4BibframeRules.FieldRule rule) {
    if (Objects.isNull(rule.getControlFields())) {
      return Collections.emptyList();
    }
    return rule.getControlFields()
      .entrySet()
      .stream()
      .map(entry -> new ControlFieldsPropertyBuilder(entry.getKey(), entry.getValue(), dictionaryProcessor))
      .collect(Collectors.toList());
  }

  private Collection<Property> getConstants(Marc4BibframeRules.FieldRule rule) {
    if (Objects.isNull(rule.getConstants())) {
      return Collections.emptyList();
    }
    return rule.getConstants()
      .entrySet()
      .stream()
      .map(entry -> new Property(entry.getKey(), entry.getValue()))
      .toList();
  }
}
