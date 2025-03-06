package org.folio.marc4ld.mapper.field008.supplementary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.PredicateDictionary.SUPPLEMENTARY_CONTENT;
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

class Marc2LdSupplementaryContentIT extends Marc2LdTestBase {

  @Test
  void shouldMapSupplementaryContent() {
    // given
    var marc = loadResourceAsString("fields/008/marc_008_supplementary_content.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(ResourceEdgeHelper::getWorkEdge)
      .extracting(workEdge -> getOutgoingEdges(workEdge, withPredicateUri("http://bibfra.me/vocab/marc/supplementaryContent")))
      .satisfies(edges -> {
        assertThat(edges).hasSize(4);
        validateEdge(edges.getFirst(), SUPPLEMENTARY_CONTENT, List.of(CATEGORY),
          Map.of(
            "http://bibfra.me/vocab/marc/code", List.of("bibliography"),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/msupplcont/bibliography"),
            "http://bibfra.me/vocab/marc/term", List.of("bibliography")
          ), "bibliography");
        validateEdge(edges.get(1), SUPPLEMENTARY_CONTENT, List.of(CATEGORY),
          Map.of(
            "http://bibfra.me/vocab/marc/code", List.of("filmography"),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/msupplcont/filmography"),
            "http://bibfra.me/vocab/marc/term", List.of("filmography")
          ), "filmography");
        validateEdge(edges.get(2), SUPPLEMENTARY_CONTENT, List.of(CATEGORY),
          Map.of(
            "http://bibfra.me/vocab/marc/code", List.of("discography"),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/msupplcont/discography"),
            "http://bibfra.me/vocab/marc/term", List.of("discography")
          ), "discography");
        validateEdge(edges.get(3), SUPPLEMENTARY_CONTENT, List.of(CATEGORY),
          Map.of(
            "http://bibfra.me/vocab/marc/code", List.of("index"),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/msupplcont/index"),
            "http://bibfra.me/vocab/marc/term", List.of("index")
          ), "index");
      })
      .extracting(edges -> getOutgoingEdges(edges.getFirst()))
      .satisfies(edges -> {
        assertThat(edges).hasSize(1);
        validateEdge(edges.getFirst(), IS_DEFINED_BY, List.of(CATEGORY_SET),
          Map.of(
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/msupplcont"),
            "http://bibfra.me/vocab/lite/label", List.of("Supplementary Content")
          ), "Supplementary Content");
        assertThat(getOutgoingEdges(edges.getFirst())).isEmpty();
      });
  }
}
