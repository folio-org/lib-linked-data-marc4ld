package org.folio.marc4ld.configuration;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ABBREVIATED_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.AGENT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FREQUENCY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_CODEN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.LIGHT_RESOURCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.folio.ld.fingerprint.config.FingerprintRules;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Marc4LdFingerprintRulesValidationIT {

  private static final Set<Set<String>> EXCLUDED_TYPE_COMBINATION = Set.of(
    Set.of(WORK.name(), LIGHT_RESOURCE.name()), // covered by partialTypesMatch rule for WORK type
    Set.of(INSTANCE.name(), LIGHT_RESOURCE.name()), // covered by partialTypesMatch rule for INSTANCE type
    Set.of(ABBREVIATED_TITLE.name()), // use all properties for fingerprinting
    Set.of(ID_CODEN.name(), IDENTIFIER.name()), // use all properties for fingerprinting
    Set.of(AGENT.name()), // use all properties for fingerprinting
    Set.of(FREQUENCY.name()), // use all properties for fingerprinting
    Set.of(IDENTIFIER.name()) // use all properties for fingerprinting
  );

  @Autowired
  private Marc4LdRules marc4LdRules;

  @Autowired
  private FingerprintRules fingerprintRules;

  @Test
  void shouldContainFingerprintRuleForEveryMarc4LdTypeCombination() {
    var marc4LdTypeCombinations = extractMarc4LdTypeCombinations();
    marc4LdTypeCombinations.removeAll(EXCLUDED_TYPE_COMBINATION);
    var fingerprintTypeCombinations = extractFingerprintTypeCombinations();

    var missingTypeCombinations = new LinkedHashSet<>(marc4LdTypeCombinations);
    missingTypeCombinations.removeAll(fingerprintTypeCombinations);

    assertThat(missingTypeCombinations)
      .withFailMessage("Missing fingerprint rules for type combinations: %s", missingTypeCombinations)
      .isEmpty();
  }

  private Set<Set<String>> extractMarc4LdTypeCombinations() {
    return Stream.of(
        extractFromFieldRulesMap(marc4LdRules.getBibFieldRules()),
        extractFromFieldRulesMap(marc4LdRules.getAuthorityFieldRules()))
      .flatMap(stream -> stream)
      .collect(toSet());
  }

  private Set<Set<String>> extractFingerprintTypeCombinations() {
    return ofNullable(fingerprintRules.getRules())
      .stream()
      .flatMap(Collection::stream)
      .map(FingerprintRules.FingerprintRule::types)
      .map(this::toTypeCombination)
      .filter(Objects::nonNull)
      .collect(toSet());
  }

  private Stream<Set<String>> extractFromFieldRulesMap(
    Map<String, ? extends Collection<Marc4LdRules.FieldRule>> rulesMap
  ) {
    return ofNullable(rulesMap)
      .stream()
      .flatMap(map -> map.values().stream())
      .filter(Objects::nonNull)
      .flatMap(Collection::stream)
      .flatMap(this::extractFromRule);
  }

  private Stream<Set<String>> extractFromRule(Marc4LdRules.FieldRule rule) {
    var currentTypes = Stream.of(toTypeCombination(rule.getTypes()))
      .filter(Objects::nonNull);
    var edgeTypes = ofNullable(rule.getEdges())
      .stream()
      .flatMap(Collection::stream)
      .flatMap(this::extractFromRule);
    return Stream.concat(currentTypes, edgeTypes);
  }

  private Set<String> toTypeCombination(Set<String> types) {
    return ofNullable(types)
      .filter(typeSet -> !typeSet.isEmpty())
      .orElse(null);
  }
}
