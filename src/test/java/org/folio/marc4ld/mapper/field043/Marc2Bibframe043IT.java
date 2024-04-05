package org.folio.marc4ld.mapper.field043;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.marc2ld.Marc2BibframeMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Marc2Bibframe043IT {

  @Autowired
  private Marc2BibframeMapperImpl marc2BibframeMapper;

  @Test
  void whenMarcField043WithMultipleSubfield_a_map_shouldConvertOnlyTheFirstOne() {
    // given
    var marc = loadResourceAsString("fields/043/marc_043_with_multiple_subfield_a.jsonl");

    // when
    var result = marc2BibframeMapper.fromMarcJson(marc);

    // then
    var work = result.getOutgoingEdges().iterator().next().getTarget();
    assertThat(work.getOutgoingEdges())
      .hasSize(1);
    var resourceEdge = work.getOutgoingEdges().iterator().next();
    validateEdge(
      resourceEdge,
      PredicateDictionary.GEOGRAPHIC_COVERAGE,
      List.of(ResourceTypeDictionary.PLACE),
      Map.of(
        "http://bibfra.me/vocab/marc/geographicCoverage", "https://id.loc.gov/vocabulary/geographicAreas/n-us",
        "http://bibfra.me/vocab/lite/name", "United States",
        "http://bibfra.me/vocab/marc/geographicAreaCode", "n-us"
      ),
      "United States"
    );
  }
}
