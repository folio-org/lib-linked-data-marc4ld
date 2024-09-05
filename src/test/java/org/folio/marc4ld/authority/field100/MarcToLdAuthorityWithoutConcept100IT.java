package org.folio.marc4ld.authority.field100;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getEdges;

import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.mapper.test.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MarcToLdAuthorityWithoutConcept100IT extends Marc2LdTestBase {

  @ParameterizedTest
  @CsvSource(value = {
    "PERSON , authority/100/marc_100_concept_person_with_v.jsonl",
    "PERSON , authority/100/marc_100_concept_person_with_x.jsonl",
    "PERSON , authority/100/marc_100_concept_person_with_y.jsonl",
    "PERSON , authority/100/marc_100_concept_person_with_z.jsonl",
    "FAMILY , authority/100/marc_100_concept_family_with_v.jsonl",
    "FAMILY , authority/100/marc_100_concept_family_with_x.jsonl",
    "FAMILY , authority/100/marc_100_concept_family_with_y.jsonl",
    "FAMILY , authority/100/marc_100_concept_family_with_z.jsonl"
  })
  void shouldNotMapField100_withSimpleResource(ResourceTypeDictionary mainType, String file) {
    // given
    var marc = loadResourceAsString(file);

    //when
    var result = marcAuthorityToResources(marc);

    //then
    assertThat(result)
      .filteredOn(resource -> CollectionUtils.isEqualCollection(resource.getTypes(), List.of(mainType)))
      .isEmpty();
  }

  @Test
  void shouldMapField100_withoutIdentifier() {
    // given
    var marc = loadResourceAsString("authority/100/marc_100_empty_identifier.jsonl");

    // when
    var result = marcAuthorityToResources(marc);

    // then
    assertThat(result)
      .isNotNull()
      .singleElement()
      .extracting(Resource::getOutgoingEdges)
      .satisfies(edges -> assertThat(edges).isEmpty());
  }

  @ParameterizedTest
  @CsvSource(value = {
    "PERSON , authority/100/marc_100_person.jsonl",
    "FAMILY , authority/100/marc_100_family.jsonl",
  })
  void shouldMapField100_withSimpleResource(ResourceTypeDictionary resourceType, String file) {
    // given
    var marc = loadResourceAsString(file);

    //when
    var result = marcAuthorityToResources(marc);

    //then
    assertThat(result)
      .isNotNull()
      .isNotEmpty()
      .singleElement()
      .satisfies(resource -> validateRootResource(resource, resourceType))
      .satisfies(this::validateIdentifier);
  }

  private void validateRootResource(Resource resource, ResourceTypeDictionary type) {
    TestUtil.validateResource(resource, List.of(type),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("aValue"),
        "http://bibfra.me/vocab/marc/numeration", List.of("bValue"),
        "http://bibfra.me/vocab/marc/titles", List.of("cValue1", "cValue2"),
        "http://bibfra.me/vocab/lite/date", List.of("dValue"),
        "http://bibfra.me/vocab/marc/attribution", List.of("jValue1", "jValue2"),
        "http://bibfra.me/vocab/lite/nameAlternative", List.of("qValue"),
        "http://library.link/vocab/resourcePreferred", List.of("true")
      ),
      "bValue, aValue, cValue1, cValue2, qValue, dValue");
  }

  private void validateIdentifier(Resource resource) {
    var resourceEdges = getEdges(resource, ID_LCCN, IDENTIFIER);
    assertThat(resourceEdges)
      .hasSize(1)
      .singleElement()
      .satisfies(edge ->
        validateEdge(edge, MAP, List.of(ID_LCCN, IDENTIFIER),
          Map.of(
            "http://bibfra.me/vocab/lite/name", List.of("010fieldvalue"),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/authorities/010fieldvalue"),
            "http://bibfra.me/vocab/lite/label", List.of("010fieldvalue")
          ),
          "010fieldvalue"));
  }
}
