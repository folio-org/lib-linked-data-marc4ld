package org.folio.marc4ld.authority.field110;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
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
import static org.folio.ld.dictionary.ResourceTypeDictionary.FORM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.JURISDICTION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TEMPORAL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TOPIC;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateFocusResource;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateIdentifier;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateSubfocusResources;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getEdges;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MarcToLdAuthorityJurisdictionOrganization110IT extends Marc2LdTestBase {

  private static final String EXPECTED_MAIN_LABEL = "aValue, bValue, cValue1, cValue2, dValue -- vValue1 -- vValue2 "
    + "-- xValue1 -- xValue2 -- yValue1 -- yValue2 -- zValue1 -- zValue2";
  private static final String EXPECTED_FOCUS_LABEL = "aValue, bValue, cValue1, cValue2, dValue";
  private static final String EXPECTED_SHORT_LABEL = "aValue, bValue, cValue1, cValue2, dValue -- vValue1";
  private static final Map<ResourceTypeDictionary, Character> FIELD_CODES = Map.of(
    FORM, 'v',
    TOPIC, 'x',
    TEMPORAL, 'y',
    PLACE, 'z'
  );

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
      .satisfies(resource -> validateSubfocusResources(resource, FIELD_CODES))
      .satisfies(resource -> validateIdentifier(resource, "010fieldvalue"));
  }

  private void validateOnlyOneSubfocusResource(Resource resource) {
    var edges = getEdges(resource, FORM);
    assertThat(edges).hasSize(1)
      .singleElement()
      .satisfies(e -> assertEquals(e.getPredicate(), SUB_FOCUS));
  }

  private void validateCreatedResource(Resource resource, List<ResourceTypeDictionary> types,
                                       Map<String, List<String>> properties,
                                       int outgoingEdges, String label) {
    validateResource(resource, types, properties, label);
    assertThat(resource.getOutgoingEdges()).hasSize(outgoingEdges);
  }

  private Map<String, List<String>> generalProperties() {
    return Map.of(
      CHRONOLOGICAL_SUBDIVISION.getValue(), List.of("yValue1", "yValue2"),
      SUBORDINATE_UNIT.getValue(), List.of("bValue"),
      RESOURCE_PREFERRED.getValue(), List.of("true"),
      DATE.getValue(), List.of("dValue"),
      GENERAL_SUBDIVISION.getValue(), List.of("xValue1", "xValue2"),
      NAME.getValue(), List.of("aValue"),
      PropertyDictionary.PLACE.getValue(), List.of("cValue1", "cValue2"),
      GEOGRAPHIC_SUBDIVISION.getValue(), List.of("zValue1", "zValue2"),
      FORM_SUBDIVISION.getValue(), List.of("vValue1", "vValue2"),
      LABEL.getValue(), List.of(EXPECTED_MAIN_LABEL)
    );
  }

  private Map<String, List<String>> focusProperties() {
    return Map.of(
      SUBORDINATE_UNIT.getValue(), List.of("bValue"),
      DATE.getValue(), List.of("dValue"),
      NAME.getValue(), List.of("aValue"),
      PropertyDictionary.PLACE.getValue(), List.of("cValue1", "cValue2"),
      LABEL.getValue(), List.of(EXPECTED_FOCUS_LABEL)
    );
  }

  private Map<String, List<String>> shortProperties() {
    return Map.of(
      SUBORDINATE_UNIT.getValue(), List.of("bValue"),
      RESOURCE_PREFERRED.getValue(), List.of("true"),
      DATE.getValue(), List.of("dValue"),
      NAME.getValue(), List.of("aValue"),
      PropertyDictionary.PLACE.getValue(), List.of("cValue1", "cValue2"),
      FORM_SUBDIVISION.getValue(), List.of("vValue1"),
      LABEL.getValue(), List.of(EXPECTED_SHORT_LABEL)
    );
  }
}
