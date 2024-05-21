package org.folio.marc4ld.configuration;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.service.condition.ConditionChecker;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.ld2marc.field.Bibframe2MarcFieldRuleApplier;
import org.folio.marc4ld.service.ld2marc.field.impl.Bibframe2MarcFieldRuleApplierImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Bibframe2MarcRulesConfig {

  @Bean
  public Collection<Bibframe2MarcFieldRuleApplier> getRules(
    ConditionChecker conditionChecker,
    DictionaryProcessor dictionaryProcessor,
    Marc4BibframeRules marc4BibframeRules
  ) {
    var fieldRules = new HashMap<>(marc4BibframeRules.getFieldRules());
    fieldRules.replaceAll((key, value) -> extractAdditionalFieldRules(value));

    return fieldRules.entrySet()
      .stream()
      .map(fr -> generateFieldRules(fr, conditionChecker, dictionaryProcessor))
      .flatMap(Collection::stream)
      .toList();
  }

  private List<Marc4BibframeRules.FieldRule> extractAdditionalFieldRules(List<Marc4BibframeRules.FieldRule> value) {
    return value.stream()
      .flatMap(fr -> Stream.concat(Stream.of(fr), ofNullable(fr.getEdges()).orElse(emptyList()).stream()))
      .toList();
  }

  private Collection<Bibframe2MarcFieldRuleApplier> generateFieldRules(
    Map.Entry<String, List<Marc4BibframeRules.FieldRule>> frs,
    ConditionChecker conditionChecker,
    DictionaryProcessor dictionaryProcessor
  ) {
    var tag = frs.getKey();
    return frs.getValue()
      .stream()
      .map(fr -> new Bibframe2MarcFieldRuleApplierImpl(tag, fr, conditionChecker, dictionaryProcessor))
      .map(Bibframe2MarcFieldRuleApplier.class::cast)
      .toList();
  }
}
