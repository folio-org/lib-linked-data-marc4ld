package org.folio.marc4ld.mapper.field600;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
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
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.NAME_ALTERNATIVE;
import static org.folio.ld.dictionary.PropertyDictionary.NUMERATION;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.TITLES;
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
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2Marc600HubIT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @ParameterizedTest
  @CsvSource(value = {
    "PERSON, fields/600/marc_600_person_hub_out.jsonl",
    "FAMILY, fields/600/marc_600_family_hub_out.jsonl"
  })
  void shouldCreateMarc600(ResourceTypeDictionary hubCreatorType, String expectedMarcFile) {
    // given
    var expectedMarc = loadResourceAsString(expectedMarcFile);
    var resource = createInstance(hubCreatorType);

    // when
    var marc = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(marc).isEqualTo(expectedMarc);
  }

  private Resource createInstance(ResourceTypeDictionary hubCreatorType) {
    var title = createResource(
      Map.of(
        MAIN_TITLE, List.of("Candide"),
        PART_NAME, List.of("Muqaddimah"),
        PART_NUMBER, List.of("no. 2, op. 255.")
      ),
      Set.of(TITLE),
      Map.of()
    );

    var creator = createResource(
      Map.of(
        NAME, List.of("Bernstein, Leonard"),
        DATE, List.of("1918-1990"),
        TITLES, List.of("Prof."),
        NUMERATION, List.of("I"),
        NAME_ALTERNATIVE, List.of("Leonard Bernstein")
      ),
      Set.of(hubCreatorType),
      Map.of()
    );

    var language = createResource(
      Map.of(CODE, List.of("eng")),
      Set.of(LANGUAGE_CATEGORY),
      Map.of()
    );

    var hub = createResource(
      Map.of(PropertyDictionary.LANGUAGE, List.of("English", "eng")),
      Set.of(HUB),
      Map.of(
        PredicateDictionary.TITLE, List.of(title),
        CREATOR, List.of(creator),
        LANGUAGE, List.of(language)
      )
    );

    var concept = createResource(
      Map.of(
        GENERAL_SUBDIVISION, List.of("Influence"),
        FORM_SUBDIVISION, List.of("Exhibitions"),
        GEOGRAPHIC_SUBDIVISION, List.of("USA", "North America"),
        CHRONOLOGICAL_SUBDIVISION, List.of("First", "1st")
      ),
      Set.of(CONCEPT, HUB),
      Map.of(FOCUS, List.of(hub))
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
