package org.folio.marc4ld.mapper.field500;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.ResourceEdgeHelper;
import org.junit.jupiter.api.Test;

class Marc2Ld500IT extends Marc2LdTestBase {

  @Test
  void shouldMapField500() {
    // given
    var marc = loadResourceAsString("fields/500/marc_500.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    assertThat(result)
      .satisfies(resource -> validateResource(resource,
        List.of(ResourceTypeDictionary.INSTANCE),
        Map.of(
          "http://bibfra.me/vocab/lite/note", List.of("note 1", "note 2")
        ), ""
      )).extracting(ResourceEdgeHelper::getEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }
}
