package org.folio.marc4ld.authority;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class MarcToLdAuthorityWithoutLccnIT extends Marc2LdTestBase {
  @Test
  void shouldNotMapMarcAuthority_withoutLccn() {
    // given
    var marc = loadResourceAsString("authority/100/marc_100_no_lccn.jsonl");

    // when
    var result = marcAuthorityToResources(marc);

    // then
    assertThat(result)
      .isEmpty();
  }
}
