package org.folio.marc4ld.mapper.field020;

import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_ISBN;
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

class Marc2Ld020IT extends Marc2LdTestBase {
  @Test
  void shouldMapField020() {
    // given
    var marc = loadResourceAsString("fields/020/marc_020_marc2ld.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var isbnEdges = getOutgoingEdges(result, withPredicateUri(MAP.getUri()));
    assertEquals(2, isbnEdges.size());

    // Current ISBN edge (020$a)
    validateResource(
      isbnEdges.getFirst().getTarget(),
      List.of(IDENTIFIER, ID_ISBN),
      Map.of(
        "http://bibfra.me/vocab/marc/qualifier", List.of("hardcover"),
        "http://bibfra.me/vocab/lite/name", List.of("1000")
      ),
      "1000"
    );

    var currentStatusEdge = getStatusEdge(isbnEdges.getFirst().getTarget());
    validateResource(
      currentStatusEdge.getTarget(),
      List.of(ResourceTypeDictionary.STATUS),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("current"),
        "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mstatus/current")
      ),
      "current"
    );

    // Cancelled ISBN edge (020$z)
    validateResource(
      isbnEdges.get(1).getTarget(),
      List.of(IDENTIFIER, ID_ISBN),
      Map.of(
        "http://bibfra.me/vocab/marc/qualifier", List.of("black leather"),
        "http://bibfra.me/vocab/lite/name", List.of("1002")
      ),
      "1002"
    );

    var cancelledStatusEdge = getStatusEdge(isbnEdges.get(1).getTarget());
    validateResource(
      cancelledStatusEdge.getTarget(),
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
