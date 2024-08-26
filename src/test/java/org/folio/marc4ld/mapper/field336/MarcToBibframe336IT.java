package org.folio.marc4ld.mapper.field336;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CONTENT;
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
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.test.helper.ResourceEdgeHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class MarcToBibframe336IT extends Marc2LdTestBase {

  @Test
  void shouldMapField336() {
    // given
    var marc = loadResourceAsString("fields/336/marc_336_rdacontent.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(this::getContentEdge)
      .satisfies(e -> validateEdge(e, CONTENT, List.of(CATEGORY),
        Map.of(
          "http://bibfra.me/vocab/marc/code", List.of("CONTENT code"),
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/contentTypes/CONTENT code"),
          "http://bibfra.me/vocab/marc/term", List.of("CONTENT term"),
          "http://bibfra.me/vocab/marc/source", List.of("CONTENT source")
        ),
        "CONTENT term"))
      .extracting(this::getCategorySetEdge)
      .satisfies(e -> validateEdge(e, IS_DEFINED_BY, List.of(CATEGORY_SET),
        Map.of(
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/genreFormSchemes/rdacontent"),
          "http://bibfra.me/vocab/lite/label", List.of("rdacontent")
        ),
        "rdacontent"))
      .extracting(ResourceEdgeHelper::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  private ResourceEdge getContentEdge(Resource result) {
    return Optional.of(getWorkEdge(result))
      .map(this::getContentEdge)
      .orElseThrow();
  }

  private ResourceEdge getContentEdge(ResourceEdge edge) {
    return getFirstOutgoingEdge(edge, withPredicateUri("http://bibfra.me/vocab/marc/content"));
  }

  private ResourceEdge getCategorySetEdge(ResourceEdge edge) {
    return getFirstOutgoingEdge(edge, withPredicateUri("http://bibfra.me/vocab/lite/isDefinedBy"));
  }
}
