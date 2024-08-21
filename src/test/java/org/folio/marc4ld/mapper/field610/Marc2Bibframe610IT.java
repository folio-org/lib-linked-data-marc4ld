package org.folio.marc4ld.mapper.field610;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getOutgoingEdges;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getWorkEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class Marc2Bibframe610IT  extends Marc2LdTestBase {

  @ParameterizedTest
  @CsvSource(value = {
    "fields/610/marc_610_jurisdiction.jsonl, http://bibfra.me/vocab/lite/Jurisdiction",
    "fields/610/marc_610_organization.jsonl, http://bibfra.me/vocab/lite/Organization"
  })
  void mappedResource_shouldContain_allRepeatableSubFocusEdges(String marcFile, String subjectType) {
    // given
    var marc = loadResourceAsString(marcFile);

    // when
    var resource = marcBibToResource(marc);

    //then
    var work = getWorkEdge(resource).getTarget();
    var subjectEdge = getFirstOutgoingEdge(work, withPredicateUri(SUBJECT.getUri()));
    var subjectTypes = subjectEdge.getTarget().getTypes()
      .stream()
      .map(ResourceTypeDictionary::getUri)
      .toList();
    var subFocusEdges = getOutgoingEdges(subjectEdge, withPredicateUri(SUB_FOCUS.getUri()));

    assertThat(subjectTypes).containsOnly("http://bibfra.me/vocab/lite/Concept", subjectType);
    assertThat(subFocusEdges)
      .hasSize(8)
      .extracting(ResourceEdge::getTarget)
      .extracting(Resource::getLabel)
      .containsOnly("form 1", "form 2", "topic 1", "topic 2", "temporal 1", "temporal 2", "place 1", "place 2");
  }
}
