package org.folio.marc4ld.mapper.field043;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Bibframe043IT extends Marc2LdTestBase {

  @Test
  void whenMarcField043WithMultipleSubfield_a_map_shouldConvertBoth() {
    // given
    var marc = loadResourceAsString("fields/043/marc_043_with_multiple_subfield_a.jsonl");

    // when
    var result = marcBibToResource(marc);

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
        "http://bibfra.me/vocab/marc/geographicCoverage",
        List.of("https://id.loc.gov/vocabulary/geographicAreas/n-us",
          "https://id.loc.gov/vocabulary/geographicAreas/n-us-non-skip"),
        "http://bibfra.me/vocab/lite/name", List.of("United States"),
        "http://bibfra.me/vocab/lite/label", List.of("United States"),
        "http://bibfra.me/vocab/marc/geographicAreaCode", List.of("n-us", "n-us-non-skip")
      ),
      "United States"
    );
  }
}
