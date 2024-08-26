package org.folio.marc4ld.authority.field110;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

public class MarcToLdAuthorityEmptyField110IT extends Marc2LdTestBase {

  @Test
  void shouldNotMapResource() {
    // given
    var marc = loadResourceAsString("authority/110/marc_110_empty.jsonl");

    // when
    var resources = marcAuthorityToResources(marc);

    // then
    assertThat(resources).isEmpty();
  }
}
