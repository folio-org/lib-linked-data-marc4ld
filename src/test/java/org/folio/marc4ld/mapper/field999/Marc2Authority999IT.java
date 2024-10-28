package org.folio.marc4ld.mapper.field999;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Authority999IT extends Marc2LdTestBase {

  @Test
  void should_mapField999Correctly() {
    // given
    var marc = loadResourceAsString("authority/999/marc_999.jsonl");

    // when
    var resources = marcAuthorityToResources(marc);

    //then
    var actualResource = resources.stream().findFirst();
    assertThat(actualResource).isPresent();
    var folioMetadata = actualResource.get().getFolioMetadata();
    assertEquals("2165ef4b-001f-46b3-a60e-52bcdeb3d5a1", folioMetadata.getInventoryId());
    assertEquals("43d58061-decf-4d74-9747-0e1c368e861b", folioMetadata.getSrsId());
  }
}
