package org.folio.marc4ld.mapper.field545;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;

import java.util.List;
import java.util.Map;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld545IT extends Marc2LdTestBase {

  @Test
  void shouldMapField545() {
    // given
    var marc = loadResourceAsString("fields/545/marc_545_in.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    assertThat(result)
      .satisfies(instance -> validateResource(instance,
        List.of(INSTANCE),
        Map.of(
          "http://bibfra.me/vocab/marc/biogdata", List.of("biographical data"),
          "http://bibfra.me/vocab/marc/adminhist", List.of("historical data"),
          "http://bibfra.me/vocab/lite/note", List.of("545 data")
        ),
        ""));
  }
}
