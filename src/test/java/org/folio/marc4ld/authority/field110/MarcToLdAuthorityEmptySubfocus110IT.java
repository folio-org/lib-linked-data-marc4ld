package org.folio.marc4ld.authority.field110;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PLACE;
import static org.folio.ld.dictionary.PropertyDictionary.RESOURCE_PREFERRED;
import static org.folio.ld.dictionary.PropertyDictionary.SUBORDINATE_UNIT;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateIdentifier;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MarcToLdAuthorityEmptySubfocus110IT extends Marc2LdTestBase {

  private static final String EXPECTED_MAIN_LABEL = "aValue, bValue, cValue1, cValue2, dValue";
  private static final String EXPECTED_IDENTIFIER = "010fieldvalue";
  private static final Map<String, List<String>> EXPECTED_PROPERTIES = Map.of(
    NAME.getValue(), List.of("aValue"),
    SUBORDINATE_UNIT.getValue(), List.of("bValue"),
    PLACE.getValue(), List.of("cValue1", "cValue2"),
    DATE.getValue(), List.of("dValue"),
    RESOURCE_PREFERRED.getValue(), List.of("true"),
    LABEL.getValue(), List.of(EXPECTED_MAIN_LABEL)
  );

  @ParameterizedTest
  @CsvSource(value = {
    "ORGANIZATION , authority/110/marc_110_organization_empty_subfocus.jsonl",
    "JURISDICTION , authority/110/marc_110_jurisdiction_empty_subfocus.jsonl",
  })
  void shouldNotMapResource(ResourceTypeDictionary type, String file) {
    // given
    var marc = loadResourceAsString(file);

    // when
    var resources = marcAuthorityToResources(marc);

    // then
    assertThat(resources)
      .hasSize(1)
      .singleElement()
      .satisfies(resource -> validateResource(resource, List.of(type), EXPECTED_PROPERTIES, EXPECTED_MAIN_LABEL))
      .satisfies(resource -> validateIdentifier(resource, EXPECTED_IDENTIFIER));
  }
}
