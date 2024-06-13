package org.folio.marc4ld.mapper.field262;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.PE_PUBLICATION;
import static org.folio.ld.dictionary.PredicateDictionary.PROVIDER_PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PROVIDER_EVENT;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.service.marc2ld.bib.MarcBib2ldMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Marc2Bibframe262IT extends Marc2LdTestBase {

  @Autowired
  private MarcBib2ldMapper marcBib2ldMapper;

  @Test
  void shouldMapField262() {
    // given
    var marc = loadResourceAsString("fields/262/marc_262.jsonl");

    //when
    var result = marcBib2ldMapper.fromMarcJson(marc);

    // then
    assertThat(result)
      .isNotNull()
      .satisfies(this::validateAllIds)
      .extracting(this::getManufactureEdge)
      .satisfies(e -> validateEdge(e, PE_PUBLICATION, List.of(PROVIDER_EVENT),
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of("Publisher or trade name"),
          "http://bibfra.me/vocab/lite/place", List.of("Place of production"),
          "http://bibfra.me/vocab/lite/date", List.of("2001"),
          "http://bibfra.me/vocab/lite/providerDate", List.of("1995")
        ),
        "Publisher or trade name"))
      .extracting(this::getFirstTargetOutgoingEdge)
      .satisfies(e -> validateEdge(e, PROVIDER_PLACE, List.of(PLACE),
        Map.of(
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/countries/xxc"),
          "http://bibfra.me/vocab/marc/code", List.of("xxc"),
          "http://bibfra.me/vocab/lite/label", List.of("Canada"),
          "http://bibfra.me/vocab/lite/name", List.of("Canada")

        ),
        "Canada"))
      .extracting(this::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  private ResourceEdge getManufactureEdge(Resource result) {
    return result.getOutgoingEdges()
      .stream()
      .findFirst()
      .orElseThrow();
  }

  private List<ResourceEdge> getOutgoingEdges(ResourceEdge resourceEdge) {
    return resourceEdge.getTarget()
      .getOutgoingEdges()
      .stream()
      .toList();
  }

  private ResourceEdge getFirstTargetOutgoingEdge(ResourceEdge resourceEdge) {
    return getOutgoingEdges(resourceEdge)
      .stream()
      .findFirst()
      .orElseThrow();
  }
}
