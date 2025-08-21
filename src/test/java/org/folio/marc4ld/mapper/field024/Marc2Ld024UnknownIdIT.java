package org.folio.marc4ld.mapper.field024;

import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_UNKNOWN;
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
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld024UnknownIdIT extends Marc2LdTestBase {
  @Test
  void shouldMap024Ind8() {
    // given
    var marc = loadResourceAsString("fields/024/marc_024_unknown_id.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var ianEdges = getOutgoingEdges(result, withPredicateUri(MAP.getUri()));
    assertEquals(2, ianEdges.size());

    // Current Unknown ID edge (024$a)
    validateResource(
      ianEdges.getFirst().getTarget(),
      List.of(IDENTIFIER, ID_UNKNOWN),
      Map.of(
        "http://bibfra.me/vocab/marc/qualifier", List.of("q1", "q2"),
        "http://bibfra.me/vocab/lite/name", List.of("UNKNOWN-ID-01")
      ),
      "UNKNOWN-ID-01"
    );

    var currentStatusEdge = getStatusEdge(ianEdges.getFirst().getTarget());
    validateCurrentStatus(currentStatusEdge.getTarget());

    // Cancelled Unknown ID edge (024$z)
    validateResource(
      ianEdges.get(1).getTarget(),
      List.of(IDENTIFIER, ID_UNKNOWN),
      Map.of(
        "http://bibfra.me/vocab/marc/qualifier", List.of("q3"),
        "http://bibfra.me/vocab/lite/name", List.of("UNKNOWN-ID-02")
      ),
      "UNKNOWN-ID-02"
    );

    var cancelledStatusEdge = getStatusEdge(ianEdges.get(1).getTarget());
    validateCancelledStatus(cancelledStatusEdge.getTarget());
  }

  private ResourceEdge getStatusEdge(Resource resource) {
    return getFirstOutgoingEdge(resource, withPredicateUri(STATUS.getUri()));
  }
}
