package org.folio.marc4ld.authority.field111;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PropertyDictionary.CHRONOLOGICAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.FORM_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GENERAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.RESOURCE_PREFERRED;
import static org.folio.ld.dictionary.PropertyDictionary.SUBORDINATE_UNIT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.MEETING;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateFocusResource;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateIdentifier;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.PropertyDictionary;
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
      .satisfies(AuthorityValidationHelper::validateSubfocusResources)
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
    return Map.of(
      NAME.getValue(), List.of("aValue"),
      PropertyDictionary.PLACE.getValue(), List.of("cValue1", "cValue2"),
      DATE.getValue(), List.of("dValue"),
      SUBORDINATE_UNIT.getValue(), List.of("eValue"),
      FORM_SUBDIVISION.getValue(), List.of("vValue1", "vValue2"),
      GENERAL_SUBDIVISION.getValue(), List.of("xValue1", "xValue2"),
      CHRONOLOGICAL_SUBDIVISION.getValue(), List.of("yValue1", "yValue2"),
      GEOGRAPHIC_SUBDIVISION.getValue(), List.of("zValue1", "zValue2"),
      RESOURCE_PREFERRED.getValue(), List.of("true"),
      LABEL.getValue(), List.of(EXPECTED_MAIN_LABEL)
    );
  }

  private Map<String, List<String>> focusProperties() {
    return Map.of(
      NAME.getValue(), List.of("aValue"),
      PropertyDictionary.PLACE.getValue(), List.of("cValue1", "cValue2"),
      DATE.getValue(), List.of("dValue"),
      SUBORDINATE_UNIT.getValue(), List.of("eValue"),
      LABEL.getValue(), List.of(EXPECTED_FOCUS_LABEL)
    );
  }

  private Map<String, List<String>> meetingProperties() {
    return Map.of(
      NAME.getValue(), List.of("aValue"),
      PropertyDictionary.PLACE.getValue(), List.of("cValue1", "cValue2"),
      DATE.getValue(), List.of("dValue"),
      SUBORDINATE_UNIT.getValue(), List.of("eValue"),
      LABEL.getValue(), List.of(EXPECTED_FOCUS_LABEL),
      RESOURCE_PREFERRED.getValue(), List.of("true")
    );
  }
}
