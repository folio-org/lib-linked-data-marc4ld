package org.folio.marc4ld.mapper.field008;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ILLUSTRATIONS;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Bibframe2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
public class Ld2MarcIllustrationsIT {

  @Autowired
  private Bibframe2MarcMapperImpl bibframe2MarcMapper;

  @Test
  void shouldMap_onlyFour_illustrations() {
    // given
    var expectedMarc = loadResourceAsString("fields/008/marc_008_illustrations.jsonl");
    var resource = createInstance();

    // when
    var result = bibframe2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createInstance() {
    var work = createResource(
      emptyMap(),
      Set.of(WORK),
      Map.of(ILLUSTRATIONS, List.of(
        createCategory("b"),
        createCategory("c"),
        createCategory("d"),
        createCategory("e"),
        createCategory("f")
      ))
    );

    return createResource(
      emptyMap(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }

  private Resource createCategory(String code) {
    return createResource(
      Map.of(
        CODE, List.of(code)
      ),
      Set.of(CATEGORY),
      emptyMap()
    );
  }
}
