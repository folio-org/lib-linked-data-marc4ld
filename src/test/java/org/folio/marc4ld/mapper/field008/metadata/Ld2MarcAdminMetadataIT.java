package org.folio.marc4ld.mapper.field008.metadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.getInstanceWithAdminMetadata;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapper;
import org.folio.marc4ld.service.ld2marc.impl.Ld2MarcUnitedMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2MarcAdminMetadataIT {

  @Autowired
  @Qualifier(Ld2MarcUnitedMapper.NAME)
  private Ld2MarcMapper ld2MarcMapper;

  @Test
  void shouldMapField001() {
    // given
    var expectedMarc = loadResourceAsString("fields/008/marc_008_admin_metadata.jsonl");
    var resource = getInstanceWithAdminMetadata("112024");

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }
}
