package org.folio.marc4ld.mapper.field610;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PropertyDictionary.AFFILIATION;
import static org.folio.ld.dictionary.PropertyDictionary.CHRONOLOGICAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.FORM_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GENERAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.LANGUAGE;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.MISC_INFO;
import static org.folio.ld.dictionary.PropertyDictionary.MUSIC_KEY;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.PLACE;
import static org.folio.ld.dictionary.PropertyDictionary.SUBORDINATE_UNIT;
import static org.folio.ld.dictionary.PropertyDictionary.VERSION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONTINUING_RESOURCES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.JSON_MAPPER;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.JsonNode;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2Marc610HubIT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @ParameterizedTest
  @CsvSource(value = {
    "ORGANIZATION, fields/610/ld2marc_610_hub_organization.jsonl",
    "JURISDICTION, fields/610/ld2marc_610_hub_jurisdiction.jsonl"
  })
  void shouldCreateMarc610ForConceptHub(ResourceTypeDictionary creatorType, String expectedMarcFile) throws Exception {
    // given
    var resource = createInstance(creatorType);
    var expectedNode = JSON_MAPPER.readTree(loadResourceAsString(expectedMarcFile));

    // when
    var marc = ld2MarcMapper.toMarcJson(resource);
    var actualNode = JSON_MAPPER.readTree(marc);

    // then
    var expected610 = findFieldNode(expectedNode, "610");
    var actual610 = findFieldNode(actualNode, "610");
    assertThat(actual610).isEqualTo(expected610);
  }

  private Resource createInstance(ResourceTypeDictionary creatorType) {
    var title = createResource(
      Map.of(
        MAIN_TITLE, List.of("Example Title"),
        PART_NAME, List.of("Example Part Name"),
        PART_NUMBER, List.of("Example Part Number")
      ),
      Set.of(ResourceTypeDictionary.TITLE),
      Map.of()
    );

    var creator = createResource(
      Map.of(
        NAME, List.of("Example Creator"),
        SUBORDINATE_UNIT, List.of("Example Subordinate Unit"),
        PLACE, List.of("Example Place"),
        DATE, List.of("Example Date"),
        MISC_INFO, List.of("Example Misc Info"),
        AFFILIATION, List.of("Example Affiliation")
      ),
      Set.of(creatorType),
      Map.of()
    );

    var hubProperties = Map.of(
      LANGUAGE, List.of("English")
    );

    var hub = createResource(
      hubProperties,
      Set.of(HUB),
      Map.of(
        org.folio.ld.dictionary.PredicateDictionary.TITLE, List.of(title),
        CREATOR, List.of(creator)
      )
    );

    var concept = createResource(
      Map.of(
        DATE, List.of("Concept Date"),
        MUSIC_KEY, List.of("Concept Music Key"),
        VERSION, List.of("Concept Version"),
        GENERAL_SUBDIVISION, List.of("General Subdivision"),
        FORM_SUBDIVISION, List.of("Form Subdivision"),
        GEOGRAPHIC_SUBDIVISION, List.of("Geographic Subdivision"),
        CHRONOLOGICAL_SUBDIVISION, List.of("Chronological Subdivision")
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

  private JsonNode findFieldNode(JsonNode marcNode, String tag) {
    for (var field : marcNode.get("fields")) {
      if (field.has(tag)) {
        return field;
      }
    }
    throw new IllegalStateException("Field " + tag + " not found in MARC JSON.");
  }
}
