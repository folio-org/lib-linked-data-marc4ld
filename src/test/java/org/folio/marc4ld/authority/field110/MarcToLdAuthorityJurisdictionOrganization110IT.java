package org.folio.marc4ld.authority.field110;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FORM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.JURISDICTION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateFocusResource;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateIdentifier;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getEdges;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.AuthorityValidationHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class MarcToLdAuthorityJurisdictionOrganization110IT extends Marc2LdTestBase {

  private static final String EXPECTED_MAIN_LABEL = "aValue, bValue, cValue1, cValue2, dValue -- vValue1 -- vValue2 "
    + "-- xValue1 -- xValue2 -- yValue1 -- yValue2 -- zValue1 -- zValue2";
  private static final String EXPECTED_FOCUS_LABEL = "aValue, bValue, cValue1, cValue2, dValue";
  private static final String EXPECTED_SHORT_LABEL = "aValue, bValue, cValue1, cValue2, dValue -- vValue1";

  private static Stream<Arguments> arguments() {
    return Stream.of(
      Arguments.of(
        loadResourceAsString("authority/110/marc_110_concept_jurisdiction.jsonl"),
        List.of(CONCEPT, JURISDICTION)
      ),
      Arguments.of(
        loadResourceAsString("authority/110/marc_110_concept_organization.jsonl"),
        List.of(CONCEPT, ORGANIZATION)
      ));
  }

  @Test
  void shouldMap110FieldIfOnlyOneFieldPresent() {
    // given
    var marc = loadResourceAsString("authority/110/marc_110_single_field.jsonl");
    var outgoingEdges = 3;

    // when
    var resources = marcAuthorityToResources(marc);
    // then
    assertThat(resources)
      .hasSize(1)
      .singleElement()
      .satisfies(
        resource -> validateCreatedResource(resource, List.of(CONCEPT, JURISDICTION), shortProperties(),
          outgoingEdges, EXPECTED_SHORT_LABEL))
      .satisfies(resource -> validateFocusResource(resource, JURISDICTION, focusProperties(), EXPECTED_FOCUS_LABEL))
      .satisfies(this::validateOnlyOneSubfocusResource)
      .satisfies(resource -> validateIdentifier(resource, "010fieldvalue"));
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "authority/110/marc_110_with_$t.jsonl",
    "authority/110/marc_110_with_$t_empty_subfocus.jsonl"}
  )
  void shouldNotMap110FieldIfTitleSubfieldIsPresent(String file) {
    // given
    var marc = loadResourceAsString(file);

    // when
    var resources = marcAuthorityToResources(marc);

    // then
    assertThat(resources).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("arguments")
  void shouldMap110FieldCorrectly(String marc, List<ResourceTypeDictionary> types) {
    // when
    var resources = marcAuthorityToResources(marc);
    var outgoingEdges = 10;

    //then
    assertThat(resources)
      .hasSize(1)
      .singleElement()
      .satisfies(
        resource -> validateCreatedResource(resource, types, generalProperties(),
          outgoingEdges, EXPECTED_MAIN_LABEL))
      .satisfies(resource -> validateFocusResource(resource, types.get(1), focusProperties(), EXPECTED_FOCUS_LABEL))
      .satisfies(AuthorityValidationHelper::validateSubFocusResources)
      .satisfies(resource -> validateIdentifier(resource, "010fieldvalue"));
  }

  private void validateOnlyOneSubfocusResource(Resource resource) {
    var edges = getEdges(resource, FORM);
    assertThat(edges).hasSize(1)
      .singleElement()
      .satisfies(e -> assertEquals(SUB_FOCUS, e.getPredicate()));
  }

  private void validateCreatedResource(Resource resource, List<ResourceTypeDictionary> types,
                                       Map<String, List<String>> properties,
                                       int outgoingEdges, String label) {
    validateResource(resource, types, properties, label);
    assertThat(resource.getOutgoingEdges()).hasSize(outgoingEdges);
  }

  private Map<String, List<String>> generalProperties() {
    return Map.ofEntries(
      Map.entry("http://bibfra.me/vocab/library/chronologicalSubdivision", List.of("yValue1", "yValue2")),
      Map.entry("http://bibfra.me/vocab/library/subordinateUnit", List.of("bValue")),
      Map.entry("http://library.link/vocab/resourcePreferred", List.of("true")),
      Map.entry("http://bibfra.me/vocab/lite/date", List.of("dValue")),
      Map.entry("http://bibfra.me/vocab/library/generalSubdivision", List.of("xValue1", "xValue2")),
      Map.entry("http://bibfra.me/vocab/lite/name", List.of("aValue")),
      Map.entry("http://bibfra.me/vocab/library/place", List.of("cValue1", "cValue2")),
      Map.entry("http://bibfra.me/vocab/library/geographicSubdivision", List.of("zValue1", "zValue2")),
      Map.entry("http://bibfra.me/vocab/library/formSubdivision", List.of("vValue1", "vValue2")),
      Map.entry("http://bibfra.me/vocab/library/miscInfo", List.of("gValue1", "gValue2")),
      Map.entry("http://bibfra.me/vocab/library/numberOfParts", List.of("nValue1", "nValue2")),
      Map.entry("http://bibfra.me/vocab/lite/label", List.of(EXPECTED_MAIN_LABEL))
    );
  }

  private Map<String, List<String>> focusProperties() {
    return Map.of(
      "http://bibfra.me/vocab/library/subordinateUnit", List.of("bValue"),
      "http://bibfra.me/vocab/lite/date", List.of("dValue"),
      "http://bibfra.me/vocab/lite/name", List.of("aValue"),
      "http://bibfra.me/vocab/library/place", List.of("cValue1", "cValue2"),
      "http://bibfra.me/vocab/library/miscInfo", List.of("gValue1", "gValue2"),
      "http://bibfra.me/vocab/library/numberOfParts", List.of("nValue1", "nValue2"),
      "http://bibfra.me/vocab/lite/label", List.of(EXPECTED_FOCUS_LABEL)
    );
  }

  private Map<String, List<String>> shortProperties() {
    return Map.of(
      "http://bibfra.me/vocab/library/subordinateUnit", List.of("bValue"),
      "http://library.link/vocab/resourcePreferred", List.of("true"),
      "http://bibfra.me/vocab/lite/date", List.of("dValue"),
      "http://bibfra.me/vocab/lite/name", List.of("aValue"),
      "http://bibfra.me/vocab/library/place", List.of("cValue1", "cValue2"),
      "http://bibfra.me/vocab/library/formSubdivision", List.of("vValue1"),
      "http://bibfra.me/vocab/library/miscInfo", List.of("gValue1", "gValue2"),
      "http://bibfra.me/vocab/library/numberOfParts", List.of("nValue1", "nValue2"),
      "http://bibfra.me/vocab/lite/label", List.of(EXPECTED_SHORT_LABEL)
    );
  }
}
