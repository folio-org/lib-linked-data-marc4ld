package org.folio.marc4ld.mapper.field502;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
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
class Marc2Bibframe502IT {

  @Autowired
  private Marc2BibframeMapperImpl marc2BibframeMapper;

  @Test
  void shouldMapField502() {
    // given
    var marc = loadResourceAsString("fields/502/marc_502.jsonl");

    // when
    var result = marc2BibframeMapper.fromMarcJson(marc);

    // then
    assertThat(result)
      .isNotNull()
      .extracting(this::getDissertationEdge)
      .satisfies(e -> validateEdge(e, PredicateDictionary.DISSERTATION,
        List.of(ResourceTypeDictionary.DISSERTATION),
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("dissertation label"),
          "http://bibfra.me/vocab/marc/degree", List.of("dissertation degree"),
          "http://bibfra.me/vocab/marc/dissertationYear", List.of("dissertation year"),
          "http://bibfra.me/vocab/marc/dissertationNote", List.of("dissertation note 1", "dissertation note 2"),
          "http://bibfra.me/vocab/marc/dissertationID", List.of("dissertation ID 1", "dissertation ID 2")
        ),
        "dissertation label"))
      .extracting(this::getFirstTargetOutgoingEdge)
      .satisfies(e -> validateEdge(e, PredicateDictionary.GRANTING_INSTITUTION,
        List.of(ResourceTypeDictionary.ORGANIZATION),
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of("dissertation granting institution")
        ),
        "dissertation granting institution"))
      .extracting(this::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  private ResourceEdge getDissertationEdge(Resource workResource) {
    return workResource.getOutgoingEdges().stream()
      .filter(e -> e.getPredicate().equals(PredicateDictionary.INSTANTIATES))
      .findFirst()
      .map(this::getFirstTargetOutgoingEdge)
      .orElseThrow();
  }

  private ResourceEdge getFirstTargetOutgoingEdge(ResourceEdge resourceEdge) {
    return getOutgoingEdges(resourceEdge)
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
}
