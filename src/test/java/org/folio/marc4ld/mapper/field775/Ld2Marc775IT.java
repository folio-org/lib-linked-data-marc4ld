package org.folio.marc4ld.mapper.field775;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.getSampleInstanceWithWork;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.getSampleWork;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2Marc775IT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void map_shouldNotReturn775Field() {
    // given
    var expectedMarc = loadResourceAsString("fields/775/marc_775_skipped.jsonl");
    var instance = getSampleInstanceWithWork(getSampleWork());

    // when
    var result = ld2MarcMapper.toMarcJson(instance);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }
}
