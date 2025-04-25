package org.folio.marc4ld.mapper.field490;

import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld490IT extends Marc2LdTestBase {

  @Test
  void shouldMapField490() {
    //given
    var marc = loadResourceAsString("fields/490/marc_490.jsonl");

    //when
    var result = marcBibToResource(marc);

    System.out.println();
  }
}
