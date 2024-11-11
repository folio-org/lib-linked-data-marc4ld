package org.folio.marc4ld.configuration;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.service.condition.ConditionChecker;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.ld2marc.field.Ld2MarcFieldRuleApplier;
import org.folio.marc4ld.service.ld2marc.field.impl.Ld2MarcFieldRuleApplierImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Ld2MarcRulesConfig {

  @Bean
  public Collection<Ld2MarcFieldRuleApplier> getRules(
    ConditionChecker conditionChecker,
    DictionaryProcessor dictionaryProcessor,
    Marc4LdRules marc4LdRules
  ) {
    var fieldRules = new HashMap<>(marc4LdRules.getBibFieldRules());
    fieldRules.replaceAll((key, value) -> extractAdditionalFieldRules(value));

    return fieldRules.entrySet()
      .stream()
      .map(fr -> generateFieldRules(fr, conditionChecker, dictionaryProcessor))
      .flatMap(Collection::stream)
      .toList();
  }

  private List<Marc4LdRules.FieldRule> extractAdditionalFieldRules(List<Marc4LdRules.FieldRule> value) {
    return value.stream()
      .flatMap(fr -> Stream.concat(Stream.of(fr), ofNullable(fr.getEdges()).orElse(emptyList()).stream()))
      .toList();
  }

  private Collection<Ld2MarcFieldRuleApplier> generateFieldRules(
    Map.Entry<String, List<Marc4LdRules.FieldRule>> frs,
    ConditionChecker conditionChecker,
    DictionaryProcessor dictionaryProcessor
  ) {
    var tag = frs.getKey();
    return frs.getValue()
      .stream()
      .map(fr -> new Ld2MarcFieldRuleApplierImpl(tag, fr, conditionChecker, dictionaryProcessor))
      .map(Ld2MarcFieldRuleApplier.class::cast)
      .toList();
  }
}
