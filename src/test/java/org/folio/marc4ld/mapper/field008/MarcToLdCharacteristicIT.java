package org.folio.marc4ld.mapper.field008;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CHARACTERISTIC;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getOutgoingEdges;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.ResourceEdgeHelper;
import org.junit.jupiter.api.Test;

class MarcToLdCharacteristicIT extends Marc2LdTestBase {

  @Test
  void shouldMapCharacteristic_whenRecordIsSerial() {
    // given
    var marc = loadResourceAsString("fields/008/marc_008_characteristic.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(ResourceEdgeHelper::getWorkEdge)
      .extracting(this::getCharacteristicEdge)
      .satisfies(e -> validateEdge(e, CHARACTERISTIC, List.of(CATEGORY),
        Map.of(
          "http://bibfra.me/vocab/marc/code", List.of("g"),
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mserialpubtype/mag"),
          "http://bibfra.me/vocab/marc/term", List.of("magazine")
        ), "magazine"))
      .extracting(ResourceEdgeHelper::getCategorySetEdge)
      .satisfies(e -> validateEdge(e, IS_DEFINED_BY, List.of(CATEGORY_SET),
        Map.of(
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mserialpubtype"),
          "http://bibfra.me/vocab/lite/label", List.of("Serial Publication Type")
        ), "Serial Publication Type")
      );
  }

  @Test
  void shouldNotMapCharacteristic_whenRecordIsMonograph() {
    // given
    var marc = loadResourceAsString("fields/008/marc_008_characteristic_monograph.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(ResourceEdgeHelper::getWorkEdge)
      .extracting(ResourceEdge::getTarget)
      .extracting(work -> getOutgoingEdges(work, withPredicateUri("http://bibfra.me/vocab/marc/characteristic")))
      .satisfies(edges -> assertThat(edges).isEmpty());
  }

  private ResourceEdge getCharacteristicEdge(ResourceEdge resourceEdge) {
    return getFirstOutgoingEdge(resourceEdge, withPredicateUri("http://bibfra.me/vocab/marc/characteristic"));
  }

}
