package org.folio.marc4ld.mapper.field010;

import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
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

class Marc2Ld010IT extends Marc2LdTestBase {

  @Test
  void shouldMapField010() {
    // given
    var marc = loadResourceAsString("fields/010/marc_010_a_z.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var lccnEdges = getOutgoingEdges(result, withPredicateUri(MAP.getUri()));
    assertEquals(2, lccnEdges.size());

    // Current LCCN edge (010$a)
    validateResource(
      lccnEdges.get(0).getTarget(),
      List.of(IDENTIFIER, ID_LCCN),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("2024111111"),
        "http://bibfra.me/vocab/lite/name", List.of("2024111111")
      ),
      "2024111111"
    );

    var currentEdge = getStatusEdge(lccnEdges.get(0).getTarget());
    validateResource(
      currentEdge.getTarget(),
      List.of(ResourceTypeDictionary.STATUS),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("current"),
        "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mstatus/current")
      ),
      "current"
    );

    // Cancelled LCCN edge (010$z)
    validateResource(
      lccnEdges.get(1).getTarget(),
      List.of(IDENTIFIER, ID_LCCN),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("2024222222"),
        "http://bibfra.me/vocab/lite/name", List.of("2024222222")
      ),
      "2024222222"
    );

    var cancelledEdge = getStatusEdge(lccnEdges.get(1).getTarget());
    validateResource(
      cancelledEdge.getTarget(),
      List.of(ResourceTypeDictionary.STATUS),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("canceled or invalid"),
        "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mstatus/cancinv")
      ),
      "canceled or invalid"
    );
  }

  private ResourceEdge getStatusEdge(Resource resource) {
    return getFirstOutgoingEdge(resource, withPredicateUri(STATUS.getUri()));
  }
}
