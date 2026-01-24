package org.folio.marc4ld.mapper.field610;

import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld610HubIT extends Marc2LdTestBase {

  @Test
  void shouldMapMarc610ToHub() {
    var marc = loadResourceAsString("fields/610/marc_610_hub_jurisdiction.jsonl");
    var resource = marcBibToResource(marc);

    System.out.println(resource);
  }
}
