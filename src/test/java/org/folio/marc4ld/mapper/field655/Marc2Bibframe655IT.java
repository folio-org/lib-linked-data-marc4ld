package org.folio.marc4ld.mapper.field655;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Bibframe655IT extends Marc2LdTestBase {

  @Test
  void whenMarcField655() {
    // given
    var marc = loadResourceAsString("fields/655/marc_655.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = result.getOutgoingEdges().iterator().next().getTarget();
    assertThat(work.getOutgoingEdges())
      .hasSize(2);

    var edgeIterator = work.getOutgoingEdges().iterator();
    var resourceEdge = edgeIterator.next();
    validateEdge(
      resourceEdge,
      PredicateDictionary.SUBJECT,
      List.of(ResourceTypeDictionary.CONCEPT, ResourceTypeDictionary.FORM),
      Map.of(
        "http://bibfra.me/vocab/marc/chronologicalSubdivision", List.of("form chronological subdivision"),
        "http://bibfra.me/vocab/marc/generalSubdivision", List.of("form general subdivision"),
        "http://bibfra.me/vocab/marc/geographicCoverage", List.of("form geographic coverage"),
        "http://bibfra.me/vocab/lite/name", List.of("form name"),
        "http://bibfra.me/vocab/marc/formSubdivision", List.of("form form subdivision")
      ),
      "form name"
    );
    var resourceEdge2 = edgeIterator.next();
    validateEdge(
      resourceEdge2,
      PredicateDictionary.GENRE,
      List.of(ResourceTypeDictionary.FORM),
      Map.of(
        "http://bibfra.me/vocab/marc/geographicCoverage", List.of("form geographic coverage"),
        "http://bibfra.me/vocab/lite/name", List.of("form name"),
        "http://bibfra.me/vocab/lite/label", List.of("form name")
      ),
      "form name"
    );
  }

  @Test
  void mappedResource_shouldContain_allRepeatableSubFocusEdges() {
    // given
    var marc = loadResourceAsString("fields/655/marc_655_multiple_sub_focus.jsonl");

    // when
    var resource = marcBibToResource(marc);

    //then
    var work = getWorkEdge(resource).getTarget();
    var subjectEdge = getFirstOutgoingEdge(work, withPredicateUri(SUBJECT.getUri()));
    var subFocusEdges = getOutgoingEdges(subjectEdge, withPredicateUri(SUB_FOCUS.getUri()));

    assertThat(subFocusEdges)
      .hasSize(8)
      .extracting(ResourceEdge::getTarget)
      .extracting(Resource::getLabel)
      .containsOnly("form 1", "form 2", "topic 1", "topic 2", "temporal 1", "temporal 2", "place 1", "place 2");
  }
}
