package org.folio.marc4ld.mapper.field504;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getWorkEdge;

import java.util.List;
import java.util.Map;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld504IT extends Marc2LdTestBase {

  @Test
  void shouldMapField504() {
    // given
    var marc = loadResourceAsString("fields/504/marc_504_in.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = getWorkEdge(result).getTarget();
    assertThat(work)
      .satisfies(w -> validateResource(w,
        List.of(WORK, BOOKS),
        Map.of("http://bibfra.me/vocab/marc/bibliographyNote", List.of("504 a 1, 504 b 1", "504 a 2, 504 b 2")),
        ""));
  }
}
