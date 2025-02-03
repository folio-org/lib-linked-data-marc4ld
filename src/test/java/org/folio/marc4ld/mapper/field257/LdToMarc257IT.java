package org.folio.marc4ld.mapper.field257;

import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.AssertionsForClassTypes;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
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
class LdToMarc257IT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMapField257() {
    // given
    var expectedMarc = loadResourceAsString("fields/257/marc_257_with_multiple_subfield_a.jsonl");
    var resource = createResourceWithWorkWith257();

    //when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    AssertionsForClassTypes.assertThat(result)
      .isEqualTo(expectedMarc);
  }

  private Resource createResourceWithWorkWith257() {
    var originPlace1 = MonographTestUtil.createResource(
      Map.of(
        PropertyDictionary.NAME, List.of("France"),
        PropertyDictionary.LABEL, List.of("France")
      ),
      Set.of(PLACE),
      Collections.emptyMap()
    ).setLabel("France");

    var originPlace2 = MonographTestUtil.createResource(
      Map.of(
        PropertyDictionary.NAME, List.of("United States"),
        PropertyDictionary.LABEL, List.of("United States")
      ),
      Set.of(PLACE),
      Collections.emptyMap()
    ).setLabel("United States");

    var work = MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(WORK),
      Map.of(PredicateDictionary.ORIGIN_PLACE, List.of(originPlace1, originPlace2))
    ).setLabel("Work: label");

    return MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      Map.of(PredicateDictionary.INSTANTIATES, List.of(work))
    );
  }
}
