package org.folio.marc4ld.mapper.field338;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CARRIER;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
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
class LdToMarc338IT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMapField337() {
    // given
    var expectedMarc = loadResourceAsString("fields/338/marc_338_rdacarrier.jsonl");
    var resource = createResourceWith338();

    //when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createResourceWith338() {
    var rdaContent = MonographTestUtil.createResource(
      Map.of(
        LINK, List.of("http://id.loc.gov/vocabulary/genreFormSchemes/rdamedia"),
        LABEL, List.of("rdacarrier")
      ),
      Set.of(ResourceTypeDictionary.CATEGORY_SET),
      Collections.emptyMap()
    ).setLabel("CARRIER term");


    var mediaContent = MonographTestUtil.createResource(
      Map.of(
        LINK, List.of("http://id.loc.gov/vocabulary/mediaTypes/CONTENT code"),
        CODE, List.of("CARRIER code"),
        TERM, List.of("CARRIER term"),
        SOURCE, List.of("CARRIER source")
      ),
      Set.of(ResourceTypeDictionary.CATEGORY),
      Map.of(IS_DEFINED_BY, List.of(rdaContent))
    ).setLabel("CARRIER term");

    return MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      Map.of(CARRIER, List.of(mediaContent), INSTANTIATES, List.of(createWorkBook()))
    );
  }
}
