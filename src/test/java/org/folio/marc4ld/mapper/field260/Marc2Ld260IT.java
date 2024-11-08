package org.folio.marc4ld.mapper.field260;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.PE_PUBLICATION;
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

class Marc2Ld260IT extends Marc2LdTestBase {

  @Test
  void shouldMapField260() {
    // given
    var marc = loadResourceAsString("fields/260/marc_260.jsonl");

    //when
    var result = marcBibToResource(marc);

    // then
    assertThat(result)
      .extracting(this::getPublicationEdge)
      .satisfies(e -> validateEdge(e, PE_PUBLICATION, List.of(PROVIDER_EVENT),
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of("publisher name"),
          "http://bibfra.me/vocab/lite/place", List.of("Place of publication"),
          "http://bibfra.me/vocab/lite/date", List.of("2021"),
          "http://bibfra.me/vocab/lite/providerDate", List.of("2020")
        ),
        "publisher name"))
      .extracting(this::getProviderPlaceEdge)
      .satisfies(e -> validateEdge(e, PROVIDER_PLACE, List.of(PLACE),
        Map.of(
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/countries/ncu"),
          "http://bibfra.me/vocab/marc/code", List.of("ncu"),
          "http://bibfra.me/vocab/lite/label", List.of("North Carolina"),
          "http://bibfra.me/vocab/lite/name", List.of("North Carolina")

        ),
        "North Carolina"))
      .extracting(ResourceEdgeHelper::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  private ResourceEdge getPublicationEdge(Resource resource) {
    return getFirstOutgoingEdge(resource, withPredicateUri("http://bibfra.me/vocab/marc/publication"));
  }

  private ResourceEdge getProviderPlaceEdge(ResourceEdge edge) {
    return getFirstOutgoingEdge(edge, withPredicateUri("http://bibfra.me/vocab/lite/providerPlace"));
  }
}
