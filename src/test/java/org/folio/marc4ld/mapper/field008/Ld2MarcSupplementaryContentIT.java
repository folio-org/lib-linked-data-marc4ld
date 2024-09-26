package org.folio.marc4ld.mapper.field008;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.SUPPLEMENTARY_CONTENT;
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
import org.folio.marc4ld.service.ld2marc.Bibframe2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2MarcSupplementaryContentIT {

  @Autowired
  private Bibframe2MarcMapperImpl bibframe2MarcMapper;

  @Test
  void shouldMap_supplementaryContent() {
    // given
    var expectedMarc = loadResourceAsString("fields/008/marc_008_supplementary_content.jsonl");
    var resource = createInstance();

    // when
    var result = bibframe2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createInstance() {
    var categorySet = createCategorySet("http://id.loc.gov/vocabulary/msupplcont", "Supplementary Content");
    var work = createResource(
      emptyMap(),
      Set.of(WORK),
      Map.of(SUPPLEMENTARY_CONTENT, List.of(
        createCategory("bibliography", "http://id.loc.gov/vocabulary/msupplcont/bibliography", "bibliography",
          categorySet),
        createCategory("filmography", "http://id.loc.gov/vocabulary/msupplcont/filmography", "filmography",
          categorySet),
        createCategory("discography", "http://id.loc.gov/vocabulary/msupplcont/discography", "discography",
          categorySet),
        createCategory("music", "http://id.loc.gov/vocabulary/msupplcont/music", "music", categorySet),
        createCategory("index", "http://id.loc.gov/vocabulary/msupplcont/index", "index", categorySet)
      ))
    );

    return createResource(
      emptyMap(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }
}
