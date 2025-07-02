package org.folio.marc4ld.mapper.field600;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PropertyDictionary.ATTRIBUTION;
import static org.folio.ld.dictionary.PropertyDictionary.CHRONOLOGICAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.FORM_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GENERAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.MISC_INFO;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.NAME_ALTERNATIVE;
import static org.folio.ld.dictionary.PropertyDictionary.NUMBER_OF_PARTS;
import static org.folio.ld.dictionary.PropertyDictionary.NUMERATION;
import static org.folio.ld.dictionary.PropertyDictionary.RELATOR_TERM;
import static org.folio.ld.dictionary.PropertyDictionary.TITLES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FAMILY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2Marc600SimpleConceptIT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @ParameterizedTest
  @MethodSource("personFamilyParams")
  void shouldMapField600(ResourceTypeDictionary agentType, String expectedMarcFile) {
    // given
    var resource = createInstanceWithSubject(agentType);
    var expectedMarc = loadResourceAsString(expectedMarcFile);

    // when
    var actualMarc = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(actualMarc).isEqualTo(expectedMarc);
  }

  static List<Arguments> personFamilyParams() {
    return List.of(
      Arguments.of(PERSON, "fields/600/marc_600_person.jsonl"),
      Arguments.of(FAMILY, "fields/600/marc_600_family.jsonl")
    );
  }

  private Resource createInstanceWithSubject(ResourceTypeDictionary subjectType) {
    var lccn = createResource(
      Map.of(
        NAME, List.of("n1234567890"),
        LINK, List.of("https://id.loc.gov/authorities/names/n1234567890")
      ),
      Set.of(IDENTIFIER, ID_LCCN),
      Map.of()
    );

    var personOrFamily = createResource(
      Map.of(
        NAME, List.of("name"),
        NUMERATION, List.of("numeration"),
        NAME_ALTERNATIVE, List.of("name alternative"),
        DATE, List.of("2024"),
        MISC_INFO, List.of("misc info", "another misc info"),
        ATTRIBUTION, List.of("attribution"),
        NUMBER_OF_PARTS, List.of("number of parts", "another number of parts"),
        TITLES, List.of("title 1", "title 2")
      ),
      Set.of(subjectType),
      Map.of(MAP, List.of(lccn))
    );

    var concept = createResource(
      Map.ofEntries(
        Map.entry(NAME, List.of("name")),
        Map.entry(NUMERATION, List.of("numeration")),
        Map.entry(NAME_ALTERNATIVE, List.of("name alternative")),
        Map.entry(DATE, List.of("2024")),
        Map.entry(MISC_INFO, List.of("misc info", "another misc info")),
        Map.entry(ATTRIBUTION, List.of("attribution")),
        Map.entry(NUMBER_OF_PARTS, List.of("number of parts", "another number of parts")),
        Map.entry(RELATOR_TERM, List.of("relator")),
        Map.entry(TITLES, List.of("title 1", "title 2")),
        Map.entry(FORM_SUBDIVISION, List.of("form 1", "form 2")),
        Map.entry(GENERAL_SUBDIVISION, List.of("topic 1", "topic 2")),
        Map.entry(GEOGRAPHIC_SUBDIVISION, List.of("place 1", "place 2")),
        Map.entry(CHRONOLOGICAL_SUBDIVISION, List.of("temporal 1", "temporal 2"))
      ),
      Set.of(subjectType, CONCEPT),
      Map.of(FOCUS, List.of(personOrFamily))
    );
    var work = createResource(
      Map.of(),
      Set.of(WORK),
      Map.of(SUBJECT, List.of(concept))
    );
    return createResource(
      Map.of(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }
}
