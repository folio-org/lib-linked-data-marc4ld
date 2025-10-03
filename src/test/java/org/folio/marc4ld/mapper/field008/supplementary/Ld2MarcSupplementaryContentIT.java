package org.folio.marc4ld.mapper.field008.supplementary;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.SUPPLEMENTARY_CONTENT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONTINUING_RESOURCES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createCategory;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createCategorySet;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2MarcSupplementaryContentIT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMap_supplementaryContent_whenWorkIsBook() {
    // given
    var expectedMarc = loadResourceAsString("fields/008/marc_008_supplementary_content.jsonl");
    var resource = createInstance(BOOKS);

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  @Test
  void shouldNotMap_supplementaryContent_whenWorkIsSerial() {
    // given
    var expectedMarc = loadResourceAsString("fields/008/marc_008_empty_serial.jsonl");
    var resource = createInstance(CONTINUING_RESOURCES);

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createInstance(ResourceTypeDictionary workType) {
    var categorySet = createCategorySet("http://id.loc.gov/vocabulary/msupplcont", "Supplementary Content");
    var work = createResource(
      emptyMap(),
      Set.of(WORK, workType),
      Map.of(SUPPLEMENTARY_CONTENT, List.of(
        createCategory("bibliography", "http://id.loc.gov/vocabulary/msupplcont/bibliography", "bibliography",
          categorySet),
        createCategory("film", "http://id.loc.gov/vocabulary/msupplcont/film", "filmography",
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
