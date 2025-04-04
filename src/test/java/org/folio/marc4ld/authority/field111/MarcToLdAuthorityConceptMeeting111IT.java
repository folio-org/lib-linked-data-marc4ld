package org.folio.marc4ld.authority.field111;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.MEETING;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateFocusResource;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateIdentifier;

import java.util.List;
import java.util.Map;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.AuthorityValidationHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MarcToLdAuthorityConceptMeeting111IT extends Marc2LdTestBase {

  private static final String EXPECTED_MAIN_LABEL = "aValue, dValue, cValue1, cValue2, eValue -- vValue1 -- vValue2"
    + " -- xValue1 -- xValue2 -- yValue1 -- yValue2 -- zValue1 -- zValue2";
  private static final String EXPECTED_FOCUS_LABEL = "aValue, dValue, cValue1, cValue2, eValue";
  private static final String EXPECTED_MEETING_LABEL = "aValue, dValue, cValue1, cValue2, eValue";

  @Test
  void shouldMap111FieldCorrectly() {
    // given
    var marc = loadResourceAsString("authority/111/marc_111_concept_meeting.jsonl");

    // when
    var resources = marcAuthorityToResources(marc);

    // then
    assertThat(resources)
      .singleElement()
      .satisfies(resource -> assertThat(resource.getOutgoingEdges()).hasSize(10))
      .satisfies(
        resource -> validateResource(resource, List.of(CONCEPT, MEETING), generalProperties(), EXPECTED_MAIN_LABEL))
      .satisfies(resource -> validateFocusResource(resource, MEETING, focusProperties(), EXPECTED_FOCUS_LABEL))
      .satisfies(AuthorityValidationHelper::validateSubFocusResources)
      .satisfies(resource -> validateIdentifier(resource, "010fieldvalue"));
  }

  @Test
  void shouldMap111FieldCorrectlyWhenSubfocusFieldsAreEmpty() {
    // given
    var marc = loadResourceAsString("authority/111/marc_111_meeting_empty_subfocus.jsonl");

    // when
    var resources = marcAuthorityToResources(marc);

    //then
    assertThat(resources)
      .singleElement()
      .satisfies(resource -> assertThat(resource.getOutgoingEdges()).hasSize(1))
      .satisfies(resource -> validateResource(resource, List.of(MEETING), meetingProperties(), EXPECTED_MEETING_LABEL))
      .satisfies(resource -> validateIdentifier(resource, "010fieldvalue"));
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "authority/111/marc_111_with_$t.jsonl",
    "authority/111/marc_111_with_$t_empty_subfocus.jsonl"}
  )
  void shouldNotMap111FieldIfTitleSubfieldIsPresent(String file) {
    // given
    var marc = loadResourceAsString(file);

    // when
    var resources = marcAuthorityToResources(marc);

    // then
    assertThat(resources).isEmpty();
  }

  private Map<String, List<String>> generalProperties() {
    return Map.ofEntries(
      Map.entry("http://bibfra.me/vocab/lite/name", List.of("aValue")),
      Map.entry("http://bibfra.me/vocab/marc/place", List.of("cValue1", "cValue2")),
      Map.entry("http://bibfra.me/vocab/lite/date", List.of("dValue")),
      Map.entry("http://bibfra.me/vocab/marc/subordinateUnit", List.of("eValue")),
      Map.entry("http://bibfra.me/vocab/marc/miscInfo", List.of("gValue1", "gValue2")),
      Map.entry("http://bibfra.me/vocab/marc/numberOfParts", List.of("nValue1", "nValue2")),
      Map.entry("http://bibfra.me/vocab/marc/formSubdivision", List.of("vValue1", "vValue2")),
      Map.entry("http://bibfra.me/vocab/marc/generalSubdivision", List.of("xValue1", "xValue2")),
      Map.entry("http://bibfra.me/vocab/marc/chronologicalSubdivision", List.of("yValue1", "yValue2")),
      Map.entry("http://bibfra.me/vocab/marc/geographicSubdivision", List.of("zValue1", "zValue2")),
      Map.entry("http://library.link/vocab/resourcePreferred", List.of("true")),
      Map.entry("http://bibfra.me/vocab/lite/label", List.of(EXPECTED_MAIN_LABEL))
    );
  }

  private Map<String, List<String>> focusProperties() {
    return Map.of(
      "http://bibfra.me/vocab/lite/name", List.of("aValue"),
      "http://bibfra.me/vocab/marc/place", List.of("cValue1", "cValue2"),
      "http://bibfra.me/vocab/lite/date", List.of("dValue"),
      "http://bibfra.me/vocab/marc/subordinateUnit", List.of("eValue"),
      "http://bibfra.me/vocab/marc/miscInfo", List.of("gValue1", "gValue2"),
      "http://bibfra.me/vocab/marc/numberOfParts", List.of("nValue1", "nValue2"),
      "http://bibfra.me/vocab/lite/label", List.of(EXPECTED_FOCUS_LABEL)
    );
  }

  private Map<String, List<String>> meetingProperties() {
    return Map.of(
      "http://bibfra.me/vocab/lite/name", List.of("aValue"),
      "http://bibfra.me/vocab/marc/place", List.of("cValue1", "cValue2"),
      "http://bibfra.me/vocab/lite/date", List.of("dValue"),
      "http://bibfra.me/vocab/marc/subordinateUnit", List.of("eValue"),
      "http://bibfra.me/vocab/marc/miscInfo", List.of("gValue1", "gValue2"),
      "http://bibfra.me/vocab/marc/numberOfParts", List.of("nValue1", "nValue2"),
      "http://bibfra.me/vocab/lite/label", List.of(EXPECTED_FOCUS_LABEL),
      "http://library.link/vocab/resourcePreferred", List.of("true")
    );
  }
}
