package org.folio.marc4ld.mapper.field261;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.PE_MANUFACTURE;
import static org.folio.ld.dictionary.PredicateDictionary.PROVIDER_PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PROVIDER_EVENT;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.ResourceEdgeHelper;
import org.junit.jupiter.api.Test;

class Marc2Bibframe261IT extends Marc2LdTestBase {

  @Test
  void shouldMapField261() {
    // given
    var marc = loadResourceAsString("fields/261/marc_261.jsonl");

    //when
    var result = marcBibToResource(marc);

    // then
    assertThat(result)
      .extracting(this::getManufactureEdge)
      .satisfies(e -> validateEdge(e, PE_MANUFACTURE, List.of(PROVIDER_EVENT),
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of("Releasing company"),
          "http://bibfra.me/vocab/lite/place", List.of("Place of production"),
          "http://bibfra.me/vocab/lite/date", List.of("2000"),
          "http://bibfra.me/vocab/lite/providerDate", List.of("1999")
        ),
        "Releasing company"))
      .extracting(this::getProviderPlaceEdge)
      .satisfies(e -> validateEdge(e, PROVIDER_PLACE, List.of(PLACE),
        Map.of(
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/countries/ch"),
          "http://bibfra.me/vocab/marc/code", List.of("ch"),
          "http://bibfra.me/vocab/lite/label", List.of("China (Republic : 1949- )"),
          "http://bibfra.me/vocab/lite/name", List.of("China (Republic : 1949- )")

        ),
        "China (Republic : 1949- )"))
      .extracting(ResourceEdgeHelper::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  private ResourceEdge getManufactureEdge(Resource result) {
    return getFirstOutgoingEdge(result, withPredicateUri("http://bibfra.me/vocab/marc/manufacture"));
  }

  private ResourceEdge getProviderPlaceEdge(ResourceEdge edge) {
    return getFirstOutgoingEdge(edge, withPredicateUri("http://bibfra.me/vocab/lite/providerPlace"));
  }
}
