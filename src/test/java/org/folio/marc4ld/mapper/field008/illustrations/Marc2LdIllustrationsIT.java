package org.folio.marc4ld.mapper.field008.illustrations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ILLUSTRATIONS;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONTINUING_RESOURCES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getOutgoingEdges;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.ResourceEdgeHelper;
import org.junit.jupiter.api.Test;

class Marc2LdIllustrationsIT extends Marc2LdTestBase {

  @Test
  void shouldMapIllustrations_whenRecordIsMonograph() {
    // given
    var marc = loadResourceAsString("fields/008/marc_008_illustrations.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(ResourceEdgeHelper::getWorkEdge)
      .satisfies(we -> validateResource(we.getTarget(), List.of(WORK, BOOKS), Map.of(), ""))
      .extracting(workEdge -> getOutgoingEdges(workEdge, withPredicateUri("http://bibfra.me/vocab/marc/illustrations")))
      .satisfies(edges -> {
        assertThat(edges).hasSize(4);
        validateEdge(edges.getFirst(), ILLUSTRATIONS, List.of(CATEGORY),
          Map.of(
            "http://bibfra.me/vocab/marc/code", List.of("b"),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/millus/map"),
            "http://bibfra.me/vocab/marc/term", List.of("Maps")
          ), "Maps");
        validateEdge(edges.get(1), ILLUSTRATIONS, List.of(CATEGORY),
          Map.of(
            "http://bibfra.me/vocab/marc/code", List.of("c"),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/millus/por"),
            "http://bibfra.me/vocab/marc/term", List.of("Portraits")
          ), "Portraits");
        validateEdge(edges.get(2), ILLUSTRATIONS, List.of(CATEGORY),
          Map.of(
            "http://bibfra.me/vocab/marc/code", List.of("d"),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/millus/chr"),
            "http://bibfra.me/vocab/marc/term", List.of("Charts")
          ), "Charts");
        validateEdge(edges.get(3), ILLUSTRATIONS, List.of(CATEGORY),
          Map.of(
            "http://bibfra.me/vocab/marc/code", List.of("e"),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/millus/pln"),
            "http://bibfra.me/vocab/marc/term", List.of("Plans")
          ), "Plans");
      })
      .extracting(edges -> getOutgoingEdges(edges.getFirst()))
      .satisfies(edges -> {
        assertThat(edges).hasSize(1);
        validateEdge(edges.getFirst(), IS_DEFINED_BY, List.of(CATEGORY_SET),
          Map.of(
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/millus"),
            "http://bibfra.me/vocab/lite/label", List.of("Illustrative Content")
          ), "Illustrative Content");
        assertThat(getOutgoingEdges(edges.getFirst())).isEmpty();
      });
  }

  @Test
  void shouldNotMapIllustrations_whenRecordIsSerial() {
    // given
    var marc = loadResourceAsString("fields/008/marc_008_illustrations_serial.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(ResourceEdgeHelper::getWorkEdge)
      .satisfies(we -> validateResource(we.getTarget(), List.of(WORK, CONTINUING_RESOURCES), Map.of(), ""))
      .extracting(ResourceEdge::getTarget)
      .extracting(work -> getOutgoingEdges(work, withPredicateUri("http://bibfra.me/vocab/marc/illustrations")))
      .satisfies(edges -> assertThat(edges).isEmpty());
  }
}
