package org.folio.marc4ld.authority.field100;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getEdge;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.mapper.test.TestUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class MarcToLdAuthorityConcept100partialFieldsIT extends Marc2LdTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    "authority/100/marc_100_concept_only_a.jsonl",
    "authority/100/marc_100_concept_only_b.jsonl",
    "authority/100/marc_100_concept_only_c.jsonl",
    "authority/100/marc_100_concept_only_d.jsonl",
    "authority/100/marc_100_concept_only_g.jsonl",
    "authority/100/marc_100_concept_only_j.jsonl",
    "authority/100/marc_100_concept_only_n.jsonl",
    "authority/100/marc_100_concept_only_q.jsonl",
    "authority/100/marc_100_concept_only_a_ind1_3.jsonl",
    "authority/100/marc_100_concept_only_b_ind1_3.jsonl",
    "authority/100/marc_100_concept_only_c_ind1_3.jsonl",
    "authority/100/marc_100_concept_only_d_ind1_3.jsonl",
    "authority/100/marc_100_concept_only_g_ind1_3.jsonl",
    "authority/100/marc_100_concept_only_j_ind1_3.jsonl",
    "authority/100/marc_100_concept_only_n_ind1_3.jsonl",
    "authority/100/marc_100_concept_only_q_ind1_3.jsonl",
    "authority/100/marc_100_concept_person_only.jsonl",
    "authority/100/marc_100_concept_family_only.jsonl",
  })
  void shouldNotMapField100_withoutConditionFields(String filename) {
    // given
    var marc = loadResourceAsString(filename);

    //when
    var result = marcAuthorityToResources(marc);

    //then
    assertThat(result)
      .filteredOn(resource -> resource.getTypes().contains(CONCEPT))
      .isEmpty();
  }

  @ParameterizedTest
  @CsvSource(value = {
    "vValue , PERSON, FORM     , http://bibfra.me/vocab/marc/formSubdivision          , authority/100/marc_100_concept_person_with_v.jsonl",
    "xValue , PERSON, TOPIC    , http://bibfra.me/vocab/marc/generalSubdivision       , authority/100/marc_100_concept_person_with_x.jsonl",
    "yValue , PERSON, TEMPORAL , http://bibfra.me/vocab/marc/chronologicalSubdivision , authority/100/marc_100_concept_person_with_y.jsonl",
    "zValue , PERSON, PLACE    , http://bibfra.me/vocab/marc/geographicSubdivision    , authority/100/marc_100_concept_person_with_z.jsonl",
    "vValue , FAMILY, FORM     , http://bibfra.me/vocab/marc/formSubdivision          , authority/100/marc_100_concept_family_with_v.jsonl",
    "xValue , FAMILY, TOPIC    , http://bibfra.me/vocab/marc/generalSubdivision       , authority/100/marc_100_concept_family_with_x.jsonl",
    "yValue , FAMILY, TEMPORAL , http://bibfra.me/vocab/marc/chronologicalSubdivision , authority/100/marc_100_concept_family_with_y.jsonl",
    "zValue , FAMILY, PLACE    , http://bibfra.me/vocab/marc/geographicSubdivision    , authority/100/marc_100_concept_family_with_z.jsonl"
  })
  void shouldMapField100_withConditionFields(String value,
                                             ResourceTypeDictionary mainType,
                                             ResourceTypeDictionary subFocusType,
                                             String property,
                                             String file) {
    // given
    var marc = loadResourceAsString(file);

    //when
    var result = marcAuthorityToResources(marc);

    //then
    assertThat(result)
      .hasSize(1)
      .filteredOn(resource -> resource.getTypes().containsAll(List.of(CONCEPT, mainType)))
      .singleElement()
      .satisfies(resource -> TestUtil.validateResource(resource, List.of(CONCEPT, mainType),
        Map.ofEntries(
          Map.entry(property, List.of(value)),
          Map.entry("http://bibfra.me/vocab/lite/name", List.of("aValue")),
          Map.entry("http://bibfra.me/vocab/marc/numeration", List.of("bValue")),
          Map.entry("http://bibfra.me/vocab/marc/titles", List.of("cValue")),
          Map.entry("http://bibfra.me/vocab/lite/date", List.of("dValue")),
          Map.entry("http://bibfra.me/vocab/marc/miscInfo", List.of("gValue")),
          Map.entry("http://bibfra.me/vocab/marc/attribution", List.of("jValue")),
          Map.entry("http://bibfra.me/vocab/marc/numberOfParts", List.of("nValue")),
          Map.entry("http://bibfra.me/vocab/lite/nameAlternative", List.of("qValue")),
          Map.entry("http://library.link/vocab/resourcePreferred", List.of("true")),
          Map.entry("http://bibfra.me/vocab/lite/label", List.of("bValue, aValue, cValue, qValue, dValue -- "
            + value))
        ),
        "bValue, aValue, cValue, qValue, dValue -- " + value))
      .satisfies(resource -> assertThat(resource.getOutgoingEdges()).hasSize(3))
      .satisfies(resource -> validateFocus(resource, mainType))
      .extracting(Resource::getOutgoingEdges)
      .satisfies(edges -> {
        var subFocus = edges.stream()
          .filter(edge -> edge.getPredicate().equals(SUB_FOCUS))
          .map(ResourceEdge::getTarget)
          .map(Resource::getTypes)
          .findFirst();
        assertThat(subFocus)
          .isPresent()
          .contains(Set.of(subFocusType));
      });
  }

  private void validateFocus(Resource resource, ResourceTypeDictionary type) {
    var edge = getEdge(resource, type);
    assertThat(edge)
      .isPresent();
    validateEdge(edge.get(), FOCUS, List.of(type),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("aValue"),
        "http://bibfra.me/vocab/marc/numeration", List.of("bValue"),
        "http://bibfra.me/vocab/marc/titles", List.of("cValue"),
        "http://bibfra.me/vocab/lite/date", List.of("dValue"),
        "http://bibfra.me/vocab/marc/miscInfo", List.of("gValue"),
        "http://bibfra.me/vocab/marc/attribution", List.of("jValue"),
        "http://bibfra.me/vocab/marc/numberOfParts", List.of("nValue"),
        "http://bibfra.me/vocab/lite/nameAlternative", List.of("qValue")
      ),
      "bValue, aValue, cValue, qValue, dValue");
  }
}
