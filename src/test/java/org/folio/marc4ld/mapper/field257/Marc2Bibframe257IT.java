package org.folio.marc4ld.mapper.field257;

import static org.folio.ld.dictionary.PredicateDictionary.ORIGIN_PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.List;
import java.util.Map;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.service.marc2ld.bib.MarcBib2ldMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Marc2Bibframe257IT extends Marc2LdTestBase {

  @Autowired
  private MarcBib2ldMapper marc2BibframeMapper;

  @Test
  void whenMarcField257WithMultipleSubfield_a_map_shouldConvertAll() {
    // given
    var marc = loadResourceAsString("fields/257/marc_257_with_multiple_subfield_a.jsonl");

    // when
    var result = marc2BibframeMapper.fromMarcJson(marc);

    // then
    validateAllIds(result);
    var work = result.getOutgoingEdges().iterator().next().getTarget();
    var resourceEdge = work.getOutgoingEdges().iterator().next();
    validateEdge(
      resourceEdge,
      ORIGIN_PLACE,
      List.of(PLACE),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("France", "United States"),
        "http://bibfra.me/vocab/lite/label", List.of("France, United States")
      ),
      "France, United States"
    );
  }
}
