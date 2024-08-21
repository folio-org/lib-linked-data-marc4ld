package org.folio.marc4ld.mapper.field008;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.GOVERNMENT_PUBLICATION;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getWorkEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.ResourceEdgeHelper;
import org.junit.jupiter.api.Test;

class MarcToBibframe008IT extends Marc2LdTestBase {

  @Test
  void shouldMapField008() {
    // given
    var marc = loadResourceAsString("fields/008/marc_008_mgovtpubtype.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(this::getGovernmentPublicationEdge)
      .satisfies(e -> validateEdge(e, GOVERNMENT_PUBLICATION, List.of(CATEGORY),
        Map.of(
          "http://bibfra.me/vocab/marc/code", List.of("a"),
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mgovtpubtype/a"),
          "http://bibfra.me/vocab/marc/term", List.of("Autonomous")
        ),
        "Autonomous"))
      .extracting(this::getCategorySetEdge)
      .satisfies(e -> validateEdge(e, IS_DEFINED_BY, List.of(CATEGORY_SET),
        Map.of(
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mgovtpubtype"),
          "http://bibfra.me/vocab/lite/label", List.of("Government Publication Type")
        ),
        "Government Publication Type"))
      .extracting(ResourceEdgeHelper::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  private ResourceEdge getGovernmentPublicationEdge(Resource result) {
    return Optional.of(getWorkEdge(result))
      .map(this::getGovernmentPublicationEdge)
      .orElseThrow();
  }

  private ResourceEdge getGovernmentPublicationEdge(ResourceEdge result) {
    return getFirstOutgoingEdge(result, withPredicateUri("http://bibfra.me/vocab/marc/governmentPublication"));
  }

  private ResourceEdge getCategorySetEdge(ResourceEdge edge) {
    return getFirstOutgoingEdge(edge, withPredicateUri("http://bibfra.me/vocab/lite/isDefinedBy"));
  }
}
