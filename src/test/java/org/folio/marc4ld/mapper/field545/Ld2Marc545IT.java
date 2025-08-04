package org.folio.marc4ld.mapper.field545;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.ld.dictionary.PropertyDictionary.BIOGRAPHICAL_DATA;
import static org.folio.ld.dictionary.PropertyDictionary.HISTORICAL_DATA;
import static org.folio.ld.dictionary.PropertyDictionary.NOTE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.MonographTestUtil;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2Marc545IT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMapField545() {
    // given
    var expectedMarc = loadResourceAsString("fields/545/marc_545_out.jsonl");
    var resource = createResourceWith545();

    //when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createResourceWith545() {
    return MonographTestUtil.createResource(
      Map.of(
        BIOGRAPHICAL_DATA, List.of("biographical data"),
        HISTORICAL_DATA, List.of("historical data"),
        NOTE, List.of("545 data")
      ),
      Set.of(INSTANCE),
      Collections.emptyMap()
    );
  }
}
