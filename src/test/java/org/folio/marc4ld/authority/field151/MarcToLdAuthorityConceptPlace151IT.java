package org.folio.marc4ld.authority.field151;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PropertyDictionary.CHRONOLOGICAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.FORM_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GENERAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.MISC_INFO;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.RESOURCE_PREFERRED;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateFocusResource;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateIdentifier;

import java.util.List;
import java.util.Map;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.AuthorityValidationHelper;
import org.junit.jupiter.api.Test;

class MarcToLdAuthorityConceptPlace151IT extends Marc2LdTestBase {

  private static final String EXPECTED_MAIN_LABEL = "aValue -- vValue1 -- vValue2 -- xValue1"
    + " -- xValue2 -- yValue1 -- yValue2 -- zValue1 -- zValue2";
  private static final String EXPECTED_FOCUS_LABEL = "aValue";

  @Test
  void shouldMap151FieldCorrectly() {
    // given
    var marc = loadResourceAsString("authority/151/marc_151_concept_place.jsonl");

    // when
    var resources = marcAuthorityToResources(marc);

    // then
    assertThat(resources)
      .singleElement()
      .satisfies(resource -> assertThat(resource.getOutgoingEdges()).hasSize(10))
      .satisfies(
        resource -> validateResource(resource, List.of(CONCEPT, PLACE), generalProperties(), EXPECTED_MAIN_LABEL))
      .satisfies(resource -> validateFocusResource(resource, PLACE, focusProperties(), EXPECTED_FOCUS_LABEL))
      .satisfies(AuthorityValidationHelper::validateSubFocusResources)
      .satisfies(resource -> validateIdentifier(resource, "010fieldvalue"));
  }

  @Test
  void shouldMap151FieldCorrectlyWhenSubFocusFieldsAreEmpty() {
    // given
    var marc = loadResourceAsString("authority/151/marc_151_place_empty_subfocus.jsonl");

    // when
    var resources = marcAuthorityToResources(marc);

    //then
    assertThat(resources)
      .singleElement()
      .satisfies(resource -> assertThat(resource.getOutgoingEdges()).hasSize(2))
      .satisfies(resource -> validateResource(resource, List.of(CONCEPT, PLACE), shortProperties(),
        EXPECTED_FOCUS_LABEL))
      .satisfies(resource -> validateIdentifier(resource, "010fieldvalue"));
  }

  private Map<String, List<String>> generalProperties() {
    return Map.of(
      NAME.getValue(), List.of("aValue"),
      MISC_INFO.getValue(), List.of("bValue"),
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
      MISC_INFO.getValue(), List.of("bValue"),
      LABEL.getValue(), List.of(EXPECTED_FOCUS_LABEL)
    );
  }

  private Map<String, List<String>> shortProperties() {
    return Map.of(
      NAME.getValue(), List.of("aValue"),
      MISC_INFO.getValue(), List.of("bValue"),
      LABEL.getValue(), List.of(EXPECTED_FOCUS_LABEL),
      RESOURCE_PREFERRED.getValue(), List.of("true")
    );
  }
}
