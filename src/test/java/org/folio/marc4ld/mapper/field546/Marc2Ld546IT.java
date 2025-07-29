package org.folio.marc4ld.mapper.field546;

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

class Marc2Ld546IT extends Marc2LdTestBase {

  @Test
  void shouldMapField546() {
    // given
    var marc = loadResourceAsString("fields/546/marc_546_in.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = getWorkEdge(result).getTarget();
    assertThat(work)
      .satisfies(w -> validateResource(w,
        List.of(WORK, BOOKS),
        Map.of("http://bibfra.me/vocab/marc/languageNote", List.of("Language note 1", "Language note 2")),
        ""));
  }
}
