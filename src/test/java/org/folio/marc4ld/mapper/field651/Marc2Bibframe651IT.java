package org.folio.marc4ld.mapper.field651;

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

class Marc2Bibframe651IT extends Marc2LdTestBase {

  @Test
  void whenMarcField651WithMultipleSubfield_a_map_shouldConvertAll() {
    // given
    var marc = loadResourceAsString("fields/651/marc_651_info_multiply.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = result.getOutgoingEdges().iterator().next().getTarget();
    var resourceEdge = work.getOutgoingEdges().iterator().next();

    validateEdge(
      resourceEdge,
      PredicateDictionary.SUBJECT,
      List.of(ResourceTypeDictionary.CONCEPT, ResourceTypeDictionary.PLACE),
      Map.of(
        "http://bibfra.me/vocab/marc/miscInfo", List.of("some info", "some more info"),
        "http://bibfra.me/vocab/lite/name", List.of("Italy")
      ),
      "Italy"
    );
  }

  @Test
  void mappedResource_shouldContain_allRepeatableSubFocusEdges() {
    // given
    var marc = loadResourceAsString("fields/651/marc_651.jsonl");

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
