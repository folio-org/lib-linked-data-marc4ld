package org.folio.marc2ld.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.marc2ld.mapper.test.MonographTestUtil.getSampleInstanceResource;
import static org.folio.marc2ld.mapper.test.TestUtil.loadResourceAsString;

import org.folio.marc2ld.mapper.ld2marc.Bibframe2MarcMapperImpl;
import org.folio.marc2ld.mapper.test.SpringTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Bibframe2MarcMapperIT {

  @Autowired
  private Bibframe2MarcMapperImpl bibframe2MarcMapper;

  @Test
  void map_shouldReturnCorrectlyMappedMarcJson() {
    // given
    var resource = getSampleInstanceResource();
    var expectedMarc = loadResourceAsString("expected_marc.jsonl");

    // when
    String result = bibframe2MarcMapper.map(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

}
