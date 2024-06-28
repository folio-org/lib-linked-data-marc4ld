package org.folio.marc4ld.mapper.field264;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.PROVIDER_PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PROVIDER_EVENT;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class Marc2Bibframe264IT extends Marc2LdTestBase {

  @ParameterizedTest
  @CsvSource(value = {
    "PE_PRODUCTION , fields/264/marc_264_Ind2_equals0.jsonl",
    "PE_PUBLICATION , fields/264/marc_264_Ind2_equals1.jsonl",
    "PE_DISTRIBUTION , fields/264/marc_264_Ind2_equals2.jsonl",
    "PE_MANUFACTURE , fields/264/marc_264_Ind2_equals3.jsonl",
  })
  void shouldMapField264(PredicateDictionary predicate, String file) {
    // given
    var marc = loadResourceAsString(file);

    //when
    var result = marcBibToResource(marc);

    // then
    assertThat(result)
      .extracting(r -> getFirstOutgoingEdge(r, predicate))
      .satisfies(e -> validateEdge(e, predicate, List.of(PROVIDER_EVENT),
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of("Name of provision activity"),
          "http://bibfra.me/vocab/lite/place", List.of("Place of provision activity"),
          "http://bibfra.me/vocab/lite/date", List.of("2010"),
          "http://bibfra.me/vocab/lite/providerDate", List.of("2009")
        ),
        "Name of provision activity"))
      .extracting(this::getProviderPlaceEdge)
      .satisfies(e -> validateEdge(e, PROVIDER_PLACE, List.of(PLACE),
        Map.of(
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/countries/nyu"),
          "http://bibfra.me/vocab/marc/code", List.of("nyu"),
          "http://bibfra.me/vocab/lite/label", List.of("New York (State)"),
          "http://bibfra.me/vocab/lite/name", List.of("New York (State)")
        ),
        "New York (State)"))
      .extracting(this::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  private ResourceEdge getFirstOutgoingEdge(Resource result, PredicateDictionary predicate) {
    return getFirstOutgoingEdge(result, withPredicateUri(predicate.getUri()));
  }

  private ResourceEdge getProviderPlaceEdge(ResourceEdge edge) {
    return getFirstOutgoingEdge(edge, withPredicateUri("http://bibfra.me/vocab/lite/providerPlace"));
  }
}
