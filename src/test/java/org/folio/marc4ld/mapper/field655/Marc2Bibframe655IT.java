package org.folio.marc4ld.mapper.field655;

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
class Marc2Bibframe655IT {

  @Autowired
  private Marc2BibframeMapperImpl marc2BibframeMapper;

  @Test
  void whenMarcField655WithMultipleSubfield_a_map_shouldConvertOnlyTheFirstOne() {
    // given
    var marc = loadResourceAsString("fields/655/marc_655_with_multiple_subfield_a.jsonl");

    // when
    var result = marc2BibframeMapper.fromMarcJson(marc);

    // then
    var work = result.getOutgoingEdges().iterator().next().getTarget();
    assertThat(work.getOutgoingEdges())
      .hasSize(2);

    var edgeIterator = work.getOutgoingEdges().iterator();
    var resourceEdge = edgeIterator.next();
    validateEdge(
      resourceEdge,
      PredicateDictionary.SUBJECT,
      List.of(ResourceTypeDictionary.CONCEPT, ResourceTypeDictionary.FORM),
      Map.of(
        "http://bibfra.me/vocab/marc/chronologicalSubdivision", "form chronological subdivision",
        "http://bibfra.me/vocab/marc/generalSubdivision", "form general subdivision",
        "http://bibfra.me/vocab/marc/geographicCoverage", "form geographic coverage",
        "http://bibfra.me/vocab/lite/name", "form name",
        "http://bibfra.me/vocab/marc/formSubdivision", "form form subdivision"
      ),
      "form name"
    );
    var resourceEdge2 = edgeIterator.next();
    validateEdge(
      resourceEdge2,
      PredicateDictionary.GENRE,
      List.of(ResourceTypeDictionary.FORM),
      Map.of(
        "http://bibfra.me/vocab/marc/geographicCoverage", "form geographic coverage",
        "http://bibfra.me/vocab/lite/name", "form name"
      ),
      "form name"
    );
  }
}
