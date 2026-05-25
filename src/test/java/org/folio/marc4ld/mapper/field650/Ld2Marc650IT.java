package org.folio.marc4ld.mapper.field650;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
import static org.folio.ld.dictionary.PropertyDictionary.GENERAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TOPIC;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
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
class Ld2Marc650IT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMapField650WithConceptMapIdentifier() {
    // given
    var resource = createInstanceWithTopicConceptAndMapIdentifier();
    var expectedMarc = loadResourceAsString("fields/650/marc_650_with_map_id.jsonl");

    // when
    var actualMarc = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(actualMarc).isEqualTo(expectedMarc);
  }

  @Test
  void shouldMapField650WithFocusIdentifierAndSubFocusEdges() {
    // given
    var resource = createInstanceWithTopicConceptAndFocusIdentifier();
    var expectedMarc = loadResourceAsString("fields/650/marc_650_with_focus_id.jsonl");

    // when
    var actualMarc = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(actualMarc).isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithTopicConceptAndMapIdentifier() {
    var lccn = createResource(
      Map.of(
        NAME, List.of("sh1234567890"),
        LINK, List.of("http://id.loc.gov/authorities/subjects/sh1234567890")
      ),
      Set.of(IDENTIFIER, ID_LCCN),
      Map.of()
    );

    var concept = createResource(
      Map.of(NAME, List.of("name")),
      Set.of(TOPIC, CONCEPT),
      Map.of(MAP, List.of(lccn))
    );

    var work = createResource(Map.of(), Set.of(WORK, BOOKS), Map.of(SUBJECT, List.of(concept)));
    return createResource(Map.of(), Set.of(INSTANCE), Map.of(INSTANTIATES, List.of(work)));
  }

  private Resource createInstanceWithTopicConceptAndFocusIdentifier() {
    var lccn = createResource(
      Map.of(
        NAME, List.of("sh1234567890"),
        LINK, List.of("http://id.loc.gov/authorities/subjects/sh1234567890")
      ),
      Set.of(IDENTIFIER, ID_LCCN),
      Map.of()
    );

    var focusTopic = createResource(
      Map.of(NAME, List.of("name")),
      Set.of(TOPIC),
      Map.of(MAP, List.of(lccn))
    );

    var subFocusTopic = createResource(
      Map.of(NAME, List.of("topic 1")),
      Set.of(TOPIC),
      Map.of()
    );

    var concept = createResource(
      Map.of(
        NAME, List.of("name"),
        GENERAL_SUBDIVISION, List.of("topic 1")
      ),
      Set.of(TOPIC, CONCEPT),
      Map.of(
        FOCUS, List.of(focusTopic),
        SUB_FOCUS, List.of(subFocusTopic)
      )
    );

    var work = createResource(Map.of(), Set.of(WORK, BOOKS), Map.of(SUBJECT, List.of(concept)));
    return createResource(Map.of(), Set.of(INSTANCE), Map.of(INSTANTIATES, List.of(work)));
  }
}
