package org.folio.marc4ld.authority.field150;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TOPIC;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateFocusResource;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateIdentifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.AuthorityValidationHelper;
import org.junit.jupiter.api.Test;

class MarcToLdAuthorityConceptTopic150IT extends Marc2LdTestBase {

  private static final String EXPECTED_MAIN_LABEL = "aValue -- vValue1 -- vValue2 -- xValue1"
    + " -- xValue2 -- yValue1 -- yValue2 -- zValue1 -- zValue2";
  private static final String EXPECTED_FOCUS_LABEL = "aValue";

  @Test
  void shouldMap150toConceptTopicResource_whenSubFocusFieldsArePresent() {
    // given
    var marc = loadResourceAsString("authority/150/marc_150_with_subfocus_fields.jsonl");

    //when
    var result = marcAuthorityToResources(marc);

    // then
    assertThat(result)
      .singleElement()
      .satisfies(resource -> assertThat(resource.getOutgoingEdges()).hasSize(10))
      .satisfies(
        resource -> validateResource(resource, List.of(CONCEPT, TOPIC), conceptTopicProperties(), EXPECTED_MAIN_LABEL))
      .satisfies(resource -> validateFocusResource(resource, TOPIC, focusProperties(), EXPECTED_FOCUS_LABEL))
      .satisfies(AuthorityValidationHelper::validateSubFocusResources)
      .satisfies(resource -> validateIdentifier(resource, "010fieldvalue"));
  }

  @Test
  void shouldMap150toTopicResource_whenSubFocusFieldsAreNotPresent() {
    // given
    var marc = loadResourceAsString("authority/150/marc_150_without_subfocus_fields.jsonl");

    //when
    var result = marcAuthorityToResources(marc);

    // then
    assertThat(result)
      .singleElement()
      .satisfies(resource -> assertThat(resource.getOutgoingEdges()).hasSize(1))
      .satisfies(resource -> validateResource(resource, List.of(TOPIC), topicProperties(), EXPECTED_FOCUS_LABEL))
      .satisfies(resource -> validateIdentifier(resource, "010fieldvalue"));
  }

  private Map<String, List<String>> conceptTopicProperties() {
    return Map.of(
      "http://bibfra.me/vocab/lite/name", List.of("aValue"),
      "http://bibfra.me/vocab/library/geographicCoverage", List.of("bValue"),
      "http://bibfra.me/vocab/library/miscInfo", List.of("gValue1", "gValue2"),
      "http://bibfra.me/vocab/library/formSubdivision", List.of("vValue1", "vValue2"),
      "http://bibfra.me/vocab/library/generalSubdivision", List.of("xValue1", "xValue2"),
      "http://bibfra.me/vocab/library/chronologicalSubdivision", List.of("yValue1", "yValue2"),
      "http://bibfra.me/vocab/library/geographicSubdivision", List.of("zValue1", "zValue2"),
      "http://library.link/vocab/resourcePreferred", List.of("true"),
      "http://bibfra.me/vocab/lite/label", List.of(EXPECTED_MAIN_LABEL)
    );
  }

  private Map<String, List<String>> focusProperties() {
    return Map.of(
      "http://bibfra.me/vocab/lite/name", List.of("aValue"),
      "http://bibfra.me/vocab/library/geographicCoverage", List.of("bValue"),
      "http://bibfra.me/vocab/library/miscInfo", List.of("gValue1", "gValue2"),
      "http://bibfra.me/vocab/lite/label", List.of(EXPECTED_FOCUS_LABEL)
    );
  }

  private Map<String, List<String>> topicProperties() {
    var topicProperties = new HashMap<>(focusProperties());
    topicProperties.put("http://library.link/vocab/resourcePreferred", List.of("true"));
    return topicProperties;
  }
}
