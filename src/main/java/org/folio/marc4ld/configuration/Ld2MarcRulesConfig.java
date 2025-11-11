package org.folio.marc4ld.configuration;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.ld2marc.condition.Ld2MarcConditionChecker;
import org.folio.marc4ld.service.ld2marc.field.Ld2MarcFieldRuleApplier;
import org.folio.marc4ld.service.ld2marc.field.impl.Ld2MarcFieldRuleApplierImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Ld2MarcRulesConfig {

  @Bean
  public Collection<Ld2MarcFieldRuleApplier> getRules(
    Ld2MarcConditionChecker conditionChecker,
    DictionaryProcessor dictionaryProcessor,
    Marc4LdRules marc4LdRules
  ) {
    var fieldRules = new HashMap<>(marc4LdRules.getBibFieldRules());

    return fieldRules.entrySet()
      .stream()
      .map(fr -> generateFieldRules(fr.getKey(), fr.getValue(), conditionChecker, dictionaryProcessor, null))
      .flatMap(Collection::stream)
      .toList();
  }

  private Collection<Ld2MarcFieldRuleApplier> generateFieldRules(
    String tag,
    List<Marc4LdRules.FieldRule> frs,
    Ld2MarcConditionChecker conditionChecker,
    DictionaryProcessor dictionaryProcessor,
    Ld2MarcFieldRuleApplier parentRule
  ) {
    if (isEmpty(frs)) {
      return List.of();
    }
    return frs
      .stream()
      .flatMap(fr -> {
        var ruleApplier = new Ld2MarcFieldRuleApplierImpl(tag, fr, conditionChecker, dictionaryProcessor, parentRule);
        var edgeAppliers = generateFieldRules(tag, fr.getEdges(), conditionChecker, dictionaryProcessor, ruleApplier);
        return Stream.concat(Stream.of(ruleApplier), edgeAppliers.stream());
      })
      .toList();
  }
}
