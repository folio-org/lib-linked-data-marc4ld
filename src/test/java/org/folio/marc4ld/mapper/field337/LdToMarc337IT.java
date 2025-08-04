package org.folio.marc4ld.mapper.field337;

import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.PredicateDictionary.MEDIA;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.SOURCE;
import static org.folio.ld.dictionary.PropertyDictionary.TERM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createWorkBook;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.AssertionsForClassTypes;
import org.folio.ld.dictionary.ResourceTypeDictionary;
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
class LdToMarc337IT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMapField337() {
    // given
    var expectedMarc = loadResourceAsString("fields/337/marc_337_rdamedia.jsonl");
    var resource = createResourceWithWorkWith337();

    //when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    AssertionsForClassTypes.assertThat(result)
      .isEqualTo(expectedMarc);
  }

  private Resource createResourceWithWorkWith337() {
    var rdaContent = MonographTestUtil.createResource(
      Map.of(
        LINK, List.of("http://id.loc.gov/vocabulary/genreFormSchemes/rdamedia"),
        LABEL, List.of("rdamedia")
      ),
      Set.of(ResourceTypeDictionary.CATEGORY_SET),
      Collections.emptyMap()
    ).setLabel("MEDIA term");


    var mediaContent = MonographTestUtil.createResource(
      Map.of(
        LINK, List.of("http://id.loc.gov/vocabulary/mediaTypes/CONTENT code"),
        CODE, List.of("MEDIA code"),
        TERM, List.of("MEDIA term"),
        SOURCE, List.of("MEDIA source")
      ),
      Set.of(ResourceTypeDictionary.CATEGORY),
      Map.of(IS_DEFINED_BY, List.of(rdaContent))
    ).setLabel("MEDIA term");

    return MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      Map.of(MEDIA, List.of(mediaContent), INSTANTIATES, List.of(createWorkBook()))
    );
  }
}
