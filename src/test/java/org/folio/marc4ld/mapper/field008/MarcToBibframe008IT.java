package org.folio.marc4ld.mapper.field008;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.GOVERNMENT_PUBLICATION;
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
import org.folio.marc4ld.service.marc2ld.bib.MarcBib2ldMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MarcToBibframe008IT extends Marc2LdTestBase {

  @Autowired
  private MarcBib2ldMapper marc2BibframeMapper;

  @Test
  void shouldMapField008() {
    // given
    var marc = loadResourceAsString("fields/008/marc_008_mgovtpubtype.jsonl");

    //when
    var result = marc2BibframeMapper.fromMarcJson(marc);

    //then
    assertThat(result)
      .isNotNull()
      .satisfies(this::validateAllIds)
      .extracting(this::getGovernmentPublicationEdge)
      .satisfies(e -> validateEdge(e, GOVERNMENT_PUBLICATION, List.of(CATEGORY),
        Map.of(
          "http://bibfra.me/vocab/marc/code", List.of("a"),
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mgovtpubtype/a"),
          "http://bibfra.me/vocab/marc/term", List.of("Autonomous")
        ),
        "Autonomous"))
      .extracting(this::getFirstTargetOutgoingEdge)
      .satisfies(e -> validateEdge(e, IS_DEFINED_BY, List.of(CATEGORY_SET),
        Map.of(
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mgovtpubtype"),
          "http://bibfra.me/vocab/lite/label", List.of("Government Publication Type")
        ),
        "Government Publication Type"))
      .extracting(this::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  private ResourceEdge getGovernmentPublicationEdge(Resource result) {
    return result.getOutgoingEdges()
      .stream()
      .findFirst()
      .map(this::getFirstTargetOutgoingEdge)
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
