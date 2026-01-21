package org.folio.marc4ld.authority.field151;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
import static org.folio.ld.dictionary.PropertyDictionary.CHRONOLOGICAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.FORM_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GENERAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.MISC_INFO;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.RESOURCE_PREFERRED;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_FAST;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCNAF;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_MESH;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
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

class MarcToLdAuthorityConceptPlace151IT extends Marc2LdTestBase {

  private static final String EXPECTED_MAIN_LABEL = "aValue -- vValue1 -- vValue2 -- xValue1"
    + " -- xValue2 -- yValue1 -- yValue2 -- zValue1 -- zValue2";
  private static final String EXPECTED_FOCUS_LABEL = "aValue";

  @Test
  void shouldMap151FieldToConceptPlaceResource_whenSubFocusFieldsArePresent() {
    // given
    var marc = loadResourceAsString("authority/151/marc_151_concept_place.jsonl");

    // when
    var resources = marcAuthorityToResources(marc);

    // then
    assertThat(resources)
      .singleElement()
      .satisfies(resource -> assertThat(resource.getOutgoingEdges()).hasSize(11))
      .satisfies(
        resource -> validateResource(resource, List.of(CONCEPT, PLACE), conceptPlaceProperties(), EXPECTED_MAIN_LABEL))
      .satisfies(resource -> validateFocusResource(resource, PLACE, focusProperties(), EXPECTED_FOCUS_LABEL))
      .satisfies(AuthorityValidationHelper::validateSubFocusResources)
      .satisfies(resource -> validateIdentifier(resource, "nr0987654321", ID_LCNAF, "http://id.loc.gov/authorities/nr0987654321"))
      .satisfies(resource -> validateIdentifier(resource, "D67890123", ID_MESH, null));
  }

  @Test
  void shouldMap151FieldToPlaceResource_whenSubFocusFieldsAreEmpty() {
    // given
    var marc = loadResourceAsString("authority/151/marc_151_place_empty_subfocus.jsonl");

    // when
    var resources = marcAuthorityToResources(marc);

    //then
    assertThat(resources)
      .singleElement()
      .satisfies(resource -> assertThat(resource.getOutgoingEdges()).hasSize(2))
      .satisfies(resource -> validateResource(resource, List.of(PLACE), placeProperties(), EXPECTED_FOCUS_LABEL))
      .satisfies(resource -> validateIdentifier(resource, "fst98765430", ID_FAST, "http://id.worldcat.org/fast/fst98765430"))
      .satisfies(resource -> validateIdentifier(resource, "ns0987654", ID_LCNAF, "http://id.loc.gov/authorities/ns0987654"));
  }

  @Test
  void shouldMap151FieldToConceptPlaceResource_withOnlySubfieldZ() {
    // given
    var marc = loadResourceAsString("authority/151/marc_151_concept_japan_tokyo.jsonl");

    // when
    var resources = marcAuthorityToResources(marc);

    // then
    assertThat(resources)
      .singleElement()
      .satisfies(resource -> assertThat(resource.getOutgoingEdges()).hasSize(3))
      .satisfies(
        resource -> validateResource(
          resource,
          List.of(CONCEPT, PLACE),
          Map.of(
            "http://library.link/vocab/resourcePreferred", List.of("true"),
            "http://bibfra.me/vocab/library/geographicSubdivision", List.of("Tokyo"),
            "http://bibfra.me/vocab/lite/name", List.of("Japan"),
            "http://bibfra.me/vocab/lite/label", List.of("Japan -- Tokyo")
          ),
          "Japan -- Tokyo")
      )
      .satisfies(
        resource -> validateFocusResource(
          resource,
          PLACE,
          Map.of(
            "http://bibfra.me/vocab/lite/label", List.of("Japan"),
            "http://bibfra.me/vocab/lite/name", List.of("Japan")
          ),
          "Japan")
      )
      .satisfies(resource -> {
        var subFocuses = resource.getOutgoingEdges().stream()
          .filter(e -> e.getPredicate().equals(SUB_FOCUS))
          .toList();
        assertThat(subFocuses).hasSize(1);
        validateResource(
          subFocuses.getFirst().getTarget(),
          List.of(PLACE),
          Map.of(
            "http://bibfra.me/vocab/lite/label", List.of("Tokyo"),
            "http://bibfra.me/vocab/lite/name", List.of("Tokyo")
          ),
          "Tokyo"
        );
      })
      .satisfies(resource -> validateIdentifier(resource, "fst01204835", ID_FAST, "http://id.worldcat.org/fast/fst01204835"));
  }

  private Map<String, List<String>> conceptPlaceProperties() {
    return Map.of(
      NAME.getValue(), List.of("aValue"),
      MISC_INFO.getValue(), List.of("gValue"),
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
      MISC_INFO.getValue(), List.of("gValue"),
      LABEL.getValue(), List.of(EXPECTED_FOCUS_LABEL)
    );
  }

  private Map<String, List<String>> placeProperties() {
    var placeProperties = new HashMap<>(focusProperties());
    placeProperties.put("http://library.link/vocab/resourcePreferred", List.of("true"));
    return placeProperties;
  }

}
