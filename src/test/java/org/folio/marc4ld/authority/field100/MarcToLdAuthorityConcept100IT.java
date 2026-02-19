package org.folio.marc4ld.authority.field100;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCSH;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCDGT;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateIdentifier;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getEdges;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.mapper.test.TestUtil;
import org.folio.marc4ld.test.helper.AuthorityValidationHelper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class MarcToLdAuthorityConcept100IT extends Marc2LdTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    "authority/100/marc_100_with_$t.jsonl",
    "authority/100/marc_100_with_$t_empty_subfocus.jsonl"}
  )
  void shouldNotMap100FieldIfTitleSubfieldIsPresent(String file) {
    // given
    var marc = loadResourceAsString(file);

    // when
    var resources = marcAuthorityToResources(marc);

    // then
    assertThat(resources).isEmpty();
  }

  @ParameterizedTest
  @CsvSource(value = {
    "PERSON , authority/100/marc_100_concept.jsonl",
    "FAMILY , authority/100/marc_100_concept_Ind1_equals3.jsonl",
  })
  void shouldMapField100(ResourceTypeDictionary resourceType, String file) {
    // given
    var marc = loadResourceAsString(file);

    //when
    var result = marcAuthorityToResources(marc);

    //then
    assertThat(result)
      .singleElement()
      .satisfies(resource -> validateRootResource(resource, List.of(CONCEPT, resourceType)))
      .satisfies(resource -> validateFocus(resource, resourceType))
      .satisfies(AuthorityValidationHelper::validateSubFocusResources)
      .satisfies(resource -> validateIdentifier(resource, "sj85121033", ID_LCCSH, "http://id.loc.gov/authorities/sj85121033"))
      .satisfies(resource -> validateIdentifier(resource, "dg8904567", ID_LCDGT, "http://id.loc.gov/authorities/dg8904567"));
  }

  private void validateRootResource(Resource resource, List<ResourceTypeDictionary> types) {
    TestUtil.validateResource(resource, types,
      Map.ofEntries(
        Map.entry("http://bibfra.me/vocab/lite/name", List.of("aValue")),
        Map.entry("http://bibfra.me/vocab/library/numeration", List.of("bValue")),
        Map.entry("http://bibfra.me/vocab/library/titles", List.of("cValue1", "cValue2")),
        Map.entry("http://bibfra.me/vocab/lite/date", List.of("dValue")),
        Map.entry("http://bibfra.me/vocab/library/miscInfo", List.of("gValue1", "gValue2")),
        Map.entry("http://bibfra.me/vocab/library/attribution", List.of("jValue1", "jValue2")),
        Map.entry("http://bibfra.me/vocab/library/numberOfParts", List.of("nValue1", "nValue2")),
        Map.entry("http://bibfra.me/vocab/lite/nameAlternative", List.of("qValue")),
        Map.entry("http://bibfra.me/vocab/library/formSubdivision", List.of("vValue1", "vValue2")),
        Map.entry("http://bibfra.me/vocab/library/generalSubdivision", List.of("xValue1", "xValue2")),
        Map.entry("http://bibfra.me/vocab/library/chronologicalSubdivision", List.of("yValue1", "yValue2")),
        Map.entry("http://bibfra.me/vocab/library/geographicSubdivision", List.of("zValue1", "zValue2")),
        Map.entry("http://library.link/vocab/resourcePreferred", List.of("true")),
        Map.entry("http://bibfra.me/vocab/lite/label", List.of("bValue, aValue, cValue1, cValue2, qValue,"
          + " dValue -- xValue1 -- xValue2 -- zValue1 -- zValue2 -- yValue1 -- yValue2 -- vValue1 -- vValue2"))
      ),
      "bValue, aValue, cValue1, cValue2, qValue, dValue"
        + " -- xValue1 -- xValue2 -- zValue1 -- zValue2 -- yValue1 -- yValue2 -- vValue1 -- vValue2");
  }

  private void validateFocus(Resource resource, ResourceTypeDictionary resourceType) {
    var resourceEdges = getEdges(resource, resourceType);
    var expectedLabel = "bValue, aValue, cValue1, cValue2, qValue, dValue";
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.getFirst(), FOCUS, List.of(resourceType),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("aValue"),
        "http://bibfra.me/vocab/library/numeration", List.of("bValue"),
        "http://bibfra.me/vocab/library/titles", List.of("cValue1", "cValue2"),
        "http://bibfra.me/vocab/lite/date", List.of("dValue"),
        "http://bibfra.me/vocab/library/miscInfo", List.of("gValue1", "gValue2"),
        "http://bibfra.me/vocab/library/attribution", List.of("jValue1", "jValue2"),
        "http://bibfra.me/vocab/library/numberOfParts", List.of("nValue1", "nValue2"),
        "http://bibfra.me/vocab/lite/nameAlternative", List.of("qValue"),
        "http://bibfra.me/vocab/lite/label", List.of(expectedLabel)
      ),
      expectedLabel);
  }
}
