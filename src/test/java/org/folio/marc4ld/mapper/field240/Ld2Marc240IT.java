package org.folio.marc4ld.mapper.field240;

import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.EXPRESSION_OF;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LANGUAGE;
import static org.folio.ld.dictionary.PropertyDictionary.LEGAL_DATE;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.MUSIC_KEY;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.NON_SORT_NUM;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.VERSION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.AssertionsForClassTypes;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2Marc240IT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldCreateMarc240FromGraph() {
    // given
    var expectedMarc = loadResourceAsString("fields/240/marc_240_out.jsonl");
    var resource = createInstanceWithHub();

    //when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    AssertionsForClassTypes.assertThat(result)
      .isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithHub() {
    var title = createResource(
      Map.of(
        MAIN_TITLE, List.of("Hub title"),
        NON_SORT_NUM, List.of("8"),
        PART_NAME, List.of("Part Name"),
        PART_NUMBER, List.of("Part Number")
      ),
      Set.of(TITLE),
      Map.of()
    );

    var organization = createResource(
      Map.of(
        NAME, List.of("Creator organization")
      ),
      Set.of(ORGANIZATION),
      Map.of()
    );

    var hub = createResource(
      Map.of(
        LABEL, List.of("Hub title"),
        LEGAL_DATE, List.of("Legal Date"),
        DATE, List.of("Date"),
        MUSIC_KEY, List.of("Music Key"),
        VERSION, List.of("Version"),
        LANGUAGE, List.of("Language name")
      ),
      Set.of(HUB),
      Map.of(
        PredicateDictionary.TITLE, List.of(title),
        CREATOR, List.of(organization)
      )
    );

    var work = createResource(
      Map.of(),
      Set.of(WORK, BOOKS),
      Map.of(EXPRESSION_OF, List.of(hub))
    );

    return createResource(
      Map.of(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }
}
