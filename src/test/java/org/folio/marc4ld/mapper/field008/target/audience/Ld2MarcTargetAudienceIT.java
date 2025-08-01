package org.folio.marc4ld.mapper.field008.target.audience;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.TARGET_AUDIENCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createCategory;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createCategorySet;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2MarcTargetAudienceIT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMap_targetAudience_whenWorkIsBook() {
    // given
    var expectedMarc = loadResourceAsString("fields/008/marc_008_target_audience.jsonl");
    var resource = createInstanceWithWorkWithTargetAudience();

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithWorkWithTargetAudience() {
    var categorySet = createCategorySet("http://id.loc.gov/vocabulary/maudience", "Target audience");
    var characteristic = createCategory("b", "http://id.loc.gov/vocabulary/maudience/pri", "Primary",
      categorySet);
    var work = createResource(
      emptyMap(),
      Set.of(WORK, BOOKS),
      Map.of(TARGET_AUDIENCE, List.of(characteristic))
    );

    return createResource(
      emptyMap(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }
}
