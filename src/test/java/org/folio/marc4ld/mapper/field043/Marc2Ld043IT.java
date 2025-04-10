package org.folio.marc4ld.mapper.field043;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.GEOGRAPHIC_COVERAGE;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.util.LdUtil.getWork;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld043IT extends Marc2LdTestBase {

  @Test
  void whenMarcField043WithMultipleSubfield_a_map_shouldConvertBoth() {
    // given
    var marc = loadResourceAsString("fields/043/marc_043_with_multiple_subfield_a.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    assertThat(getWork(result)).get()
      .satisfies(this::hasGeographicCoverageEdges);
  }

  private void hasGeographicCoverageEdges(Resource work) {
    assertThat(work.getOutgoingEdges()).hasSize(2);
    var edgesIterator = work.getOutgoingEdges().iterator();
    validateEdge(edgesIterator.next(), GEOGRAPHIC_COVERAGE, List.of(ResourceTypeDictionary.PLACE),
      Map.of(
        "http://bibfra.me/vocab/marc/geographicCoverage",
        List.of("http://id.loc.gov/vocabulary/geographicAreas/n-us"),
        "http://bibfra.me/vocab/lite/name", List.of("United States"),
        "http://bibfra.me/vocab/lite/label", List.of("United States"),
        "http://bibfra.me/vocab/marc/geographicAreaCode", List.of("n-us")
      ), "United States");
    validateEdge(edgesIterator.next(), GEOGRAPHIC_COVERAGE, List.of(ResourceTypeDictionary.PLACE),
      Map.of(
        "http://bibfra.me/vocab/marc/geographicCoverage",
        List.of("http://id.loc.gov/vocabulary/geographicAreas/a-is"),
        "http://bibfra.me/vocab/lite/name", List.of("Israel"),
        "http://bibfra.me/vocab/lite/label", List.of("Israel"),
        "http://bibfra.me/vocab/marc/geographicAreaCode", List.of("a-is")
      ), "Israel");
  }
}
