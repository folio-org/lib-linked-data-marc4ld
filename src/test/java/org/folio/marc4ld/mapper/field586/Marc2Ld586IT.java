package org.folio.marc4ld.mapper.field586;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getWorkEdge;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld586IT extends Marc2LdTestBase {

  @Test
  void shouldMapField586() {
    // given
    var marc = loadResourceAsString("fields/586/marc2ld_586.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    assertThat(getWorkEdge(result).getTarget()).satisfies(this::validateAwardsNote);
  }

  private void validateAwardsNote(Resource resource) {
    validateResource(resource, List.of(WORK),
      Map.of("http://bibfra.me/vocab/marc/awardsNote", List.of("awards_note1", "awards_note2")), "");
  }
}
