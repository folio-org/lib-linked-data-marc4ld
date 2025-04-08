package org.folio.marc4ld.mapper.field008;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.PredicateDictionary.TARGET_AUDIENCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getOutgoingEdges;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.ResourceEdgeHelper;
import org.junit.jupiter.api.Test;

class Marc2LdTargetAudienceIT extends Marc2LdTestBase {

  @Test
  void shouldMapTargetAudience() {
    // given
    var marc = loadResourceAsString("fields/008/marc_008_target_audience.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(ResourceEdgeHelper::getWorkEdge)
      .extracting(workEdge -> getOutgoingEdges(workEdge, withPredicateUri("http://bibfra.me/vocab/marc/targetAudience")))
      .satisfies(edges -> {
        assertThat(edges).hasSize(1);
        validateEdge(edges.getFirst(), TARGET_AUDIENCE, List.of(CATEGORY),
          Map.of(
          "http://bibfra.me/vocab/marc/code", List.of("b"),
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/maudience/pri"),
          "http://bibfra.me/vocab/marc/term", List.of("Primary")
          ), "Primary");
      })
      .extracting(edges -> getOutgoingEdges(edges.getFirst()))
      .satisfies(edges -> {
        assertThat(edges).hasSize(1);
        validateEdge(edges.getFirst(), IS_DEFINED_BY, List.of(CATEGORY_SET),
          Map.of(
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/maudience"),
            "http://bibfra.me/vocab/lite/label", List.of("Target audience")
          ), "Target audience");
        assertThat(getOutgoingEdges(edges.getFirst())).isEmpty();
      });
  }
}
