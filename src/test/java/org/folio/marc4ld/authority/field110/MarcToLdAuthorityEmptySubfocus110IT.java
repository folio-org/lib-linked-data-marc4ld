package org.folio.marc4ld.authority.field110;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCGFT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCMPT;
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
  private static final Map<String, List<String>> EXPECTED_PROPERTIES = Map.of(
    "http://bibfra.me/vocab/lite/name", List.of("aValue"),
    "http://bibfra.me/vocab/library/subordinateUnit", List.of("bValue"),
    "http://bibfra.me/vocab/library/place", List.of("cValue1", "cValue2"),
    "http://bibfra.me/vocab/lite/date", List.of("dValue"),
    "http://bibfra.me/vocab/library/miscInfo", List.of("gValue1", "gValue2"),
    "http://bibfra.me/vocab/library/numberOfParts", List.of("nValue1", "nValue2"),
    "http://library.link/vocab/resourcePreferred", List.of("true"),
    "http://bibfra.me/vocab/lite/label", List.of(EXPECTED_MAIN_LABEL)
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
      .satisfies(resource -> validateIdentifier(resource, "gf05121033", ID_LCGFT, "http://id.loc.gov/authorities/genreForms/gf05121033"))
      .satisfies(resource -> validateIdentifier(resource, "mp098765890", ID_LCMPT, "http://id.loc.gov/authorities/mp098765890"));
  }
}
