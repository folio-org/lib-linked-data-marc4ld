package org.folio.marc4ld.mapper.field338;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CARRIER;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class MarcToBibframe338IT extends Marc2LdTestBase {

  @Test
  void shouldMapField337() {
    // given
    var marc = loadResourceAsString("fields/338/marc_338_rdacarrier.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(this::getCarrierEdge)
      .satisfies(e -> validateEdge(e, CARRIER, List.of(CATEGORY),
        Map.of(
          "http://bibfra.me/vocab/marc/code", List.of("CARRIER code"),
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/carriers/CARRIER code"),
          "http://bibfra.me/vocab/marc/term", List.of("CARRIER term"),
          "http://bibfra.me/vocab/marc/source", List.of("CARRIER source")
        ),
        "CARRIER term"))
      .extracting(this::getCategorySetEdge)
      .satisfies(e -> validateEdge(e, IS_DEFINED_BY, List.of(CATEGORY_SET),
        Map.of(
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/genreFormSchemes/rdacarrier"),
          "http://bibfra.me/vocab/lite/label", List.of("rdacarrier")
        ),
        "rdacarrier"))
      .extracting(this::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  private ResourceEdge getCarrierEdge(Resource result) {
    return getFirstOutgoingEdge(result, withPredicateUri("http://bibfra.me/vocab/marc/carrier"));
  }

  private ResourceEdge getCategorySetEdge(ResourceEdge edge) {
    return getFirstOutgoingEdge(edge, withPredicateUri("http://bibfra.me/vocab/lite/isDefinedBy"));
  }
}
