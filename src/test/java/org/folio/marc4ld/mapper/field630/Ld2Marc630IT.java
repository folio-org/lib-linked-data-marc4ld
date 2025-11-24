package org.folio.marc4ld.mapper.field630;

import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PropertyDictionary.CHRONOLOGICAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.FORM_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GENERAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LEGAL_DATE;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.MUSIC_KEY;
import static org.folio.ld.dictionary.PropertyDictionary.NON_SORT_NUM;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.VERSION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONTINUING_RESOURCES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.LANGUAGE_CATEGORY;
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
class Ld2Marc630IT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldCreateMarc630FromGraph() {
    // given
    var expectedMarc = loadResourceAsString("fields/630/marc_630_out.jsonl");
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

    var language = createResource(
      Map.of(
        CODE, List.of("Language")
      ),
      Set.of(LANGUAGE_CATEGORY),
      Map.of()
    );

    var hub = createResource(
      Map.of(
        LABEL, List.of("Hub title"),
        LEGAL_DATE, List.of("Legal Date"),
        DATE, List.of("Date"),
        MUSIC_KEY, List.of("Music Key"),
        VERSION, List.of("Version")
      ),
      Set.of(HUB),
      Map.of(
        PredicateDictionary.TITLE, List.of(title),
        LANGUAGE, List.of(language)
      )
    );

    var concept = createResource(
      Map.of(
        LEGAL_DATE, List.of("2025"),
        DATE, List.of("2024"),
        MUSIC_KEY, List.of("Music Key 1", "Music Key 2", "Music Key 3"),
        VERSION, List.of("Version"),
        FORM_SUBDIVISION, List.of("Form1", "Form2"),
        GENERAL_SUBDIVISION, List.of("General1", "General2"),
        CHRONOLOGICAL_SUBDIVISION, List.of("Chronological1", "Chronological2"),
        GEOGRAPHIC_SUBDIVISION, List.of("Geographic subdivision")
      ),
      Set.of(HUB, CONCEPT),
      Map.of(
        FOCUS, List.of(hub)
      )
    );

    var work = createResource(
      Map.of(),
      Set.of(WORK, CONTINUING_RESOURCES),
      Map.of(SUBJECT, List.of(concept))
    );

    return createResource(
      Map.of(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }
}
