package org.folio.marc4ld.mapper.field022;

import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_ISSN;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateCancelledStatus;
import static org.folio.marc4ld.mapper.test.TestUtil.validateCurrentStatus;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getOutgoingEdges;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld022IT extends Marc2LdTestBase {
  @Test
  void shouldMapField022() {
    // given
    var marc = loadResourceAsString("fields/022/marc_022.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var issnEdges = getOutgoingEdges(result, withPredicateUri(MAP.getUri()));
    assertEquals(3, issnEdges.size());

    // Current ISSN edge (022$a)
    validateResource(
      issnEdges.getFirst().getTarget(),
      List.of(IDENTIFIER, ID_ISSN),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("0046-1111"),
        "http://bibfra.me/vocab/lite/name", List.of("0046-1111")
      ),
      "0046-1111"
    );

    var currentStatusEdge = getStatusEdge(issnEdges.getFirst().getTarget());
    validateCurrentStatus(currentStatusEdge.getTarget());

    // Incorrect ISSN edge (022$y)
    validateResource(
      issnEdges.get(1).getTarget(),
      List.of(IDENTIFIER, ID_ISSN),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("0046-2222"),
        "http://bibfra.me/vocab/lite/name", List.of("0046-2222")
      ),
      "0046-2222"
    );

    var incorrectStatusEdge = getStatusEdge(issnEdges.get(1).getTarget());
    validateResource(
      incorrectStatusEdge.getTarget(),
      List.of(ResourceTypeDictionary.STATUS),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("incorrect"),
        "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mstatus/incorrect")
      ),
      "incorrect"
    );

    // Cancelled ISSN edge (022$z)
    validateResource(
      issnEdges.get(2).getTarget(),
      List.of(IDENTIFIER, ID_ISSN),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("0046-3333"),
        "http://bibfra.me/vocab/lite/name", List.of("0046-3333")
      ),
      "0046-3333"
    );

    var cancelledStatusEdge = getStatusEdge(issnEdges.get(2).getTarget());
    validateCancelledStatus(cancelledStatusEdge.getTarget());
  }

  private ResourceEdge getStatusEdge(Resource resource) {
    return getFirstOutgoingEdge(resource, withPredicateUri(STATUS.getUri()));
  }
}
