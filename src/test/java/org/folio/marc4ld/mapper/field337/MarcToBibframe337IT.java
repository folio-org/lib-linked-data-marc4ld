package org.folio.marc4ld.mapper.field337;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.PredicateDictionary.MEDIA;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.marc2ld.Marc2BibframeMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class MarcToBibframe337IT {

  @Autowired
  private Marc2BibframeMapperImpl marc2BibframeMapper;

  @Test
  void shouldMapField337() {
    // given
    var marc = loadResourceAsString("fields/337/marc_337_rdamedia.jsonl");

    //when
    var result = marc2BibframeMapper.fromMarcJson(marc);

    //then
    assertThat(result)
      .isNotNull()
      .extracting(this::getMediaEdge)
      .satisfies(e -> validateEdge(e, MEDIA, List.of(CATEGORY),
        Map.of(
          "http://bibfra.me/vocab/marc/code", "MEDIA code",
          "http://bibfra.me/vocab/lite/link", "http://id.loc.gov/vocabulary/mediaTypes/MEDIA code",
          "http://bibfra.me/vocab/marc/term", "MEDIA term",
          "http://bibfra.me/vocab/marc/source", "MEDIA source"
        ),
        "MEDIA term"))
      .extracting(this::getFirstTargetOutgoingEdge)
      .satisfies(e -> validateEdge(e, IS_DEFINED_BY, List.of(CATEGORY_SET),
        Map.of(
          "http://bibfra.me/vocab/lite/link", "http://id.loc.gov/vocabulary/genreFormSchemes/rdamedia",
          "http://bibfra.me/vocab/lite/label", "rdamedia"
        ),
        "rdamedia"))
      .extracting(this::getOutgoingEdges)
      .asList()
      .isEmpty();
  }

  private ResourceEdge getMediaEdge(Resource result) {
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
