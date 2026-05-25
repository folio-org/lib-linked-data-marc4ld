package org.folio.marc4ld.mapper.field651;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
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
class Ld2Marc651IT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMapField651WithConceptMapIdentifier() {
    // given
    var resource = createInstanceWithPlaceConceptAndMapIdentifier();
    var expectedMarc = loadResourceAsString("fields/651/marc_651_with_map_id.jsonl");

    // when
    var actualMarc = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(actualMarc).isEqualTo(expectedMarc);
  }

  @Test
  void shouldMapField651WithFocusIdentifier() {
    // given
    var resource = createInstanceWithPlaceConceptAndFocusIdentifier();
    var expectedMarc = loadResourceAsString("fields/651/marc_651_with_focus_id.jsonl");

    // when
    var actualMarc = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(actualMarc).isEqualTo(expectedMarc);
  }

  @Test
  void shouldMapField651WithMultipleMapIdentifiers() {
    // given
    var resource = createInstanceWithPlaceConceptAndMultipleMapIdentifiers();
    var expectedMarc = loadResourceAsString("fields/651/marc_651_with_multiple_map_ids.jsonl");

    // when
    var actualMarc = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(actualMarc).isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithPlaceConceptAndMapIdentifier() {
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
      Set.of(PLACE, CONCEPT),
      Map.of(MAP, List.of(lccn))
    );

    var work = createResource(Map.of(), Set.of(WORK, BOOKS), Map.of(SUBJECT, List.of(concept)));
    return createResource(Map.of(), Set.of(INSTANCE), Map.of(INSTANTIATES, List.of(work)));
  }

  private Resource createInstanceWithPlaceConceptAndFocusIdentifier() {
    var lccn = createResource(
      Map.of(
        NAME, List.of("sh1234567890"),
        LINK, List.of("http://id.loc.gov/authorities/subjects/sh1234567890")
      ),
      Set.of(IDENTIFIER, ID_LCCN),
      Map.of()
    );

    var focusPlace = createResource(
      Map.of(NAME, List.of("name")),
      Set.of(PLACE),
      Map.of(MAP, List.of(lccn))
    );

    var concept = createResource(
      Map.of(
        NAME, List.of("name"),
        GEOGRAPHIC_SUBDIVISION, List.of("place 1")
      ),
      Set.of(PLACE, CONCEPT),
      Map.of(FOCUS, List.of(focusPlace))
    );

    var work = createResource(Map.of(), Set.of(WORK, BOOKS), Map.of(SUBJECT, List.of(concept)));
    return createResource(Map.of(), Set.of(INSTANCE), Map.of(INSTANTIATES, List.of(work)));
  }

  private Resource createInstanceWithPlaceConceptAndMultipleMapIdentifiers() {
    var lccn1 = createResource(
      Map.of(
        NAME, List.of("sh1234567890"),
        LINK, List.of("http://id.loc.gov/authorities/subjects/sh1234567890")
      ),
      Set.of(IDENTIFIER, ID_LCCN),
      Map.of()
    );

    var lccn2 = createResource(
      Map.of(
        NAME, List.of("sh9876543210"),
        LINK, List.of("http://id.loc.gov/authorities/subjects/sh9876543210")
      ),
      Set.of(IDENTIFIER, ID_LCCN),
      Map.of()
    );

    var concept = createResource(
      Map.of(
        NAME, List.of("name"),
        GEOGRAPHIC_SUBDIVISION, List.of("place 1")
      ),
      Set.of(PLACE, CONCEPT),
      Map.of(MAP, List.of(lccn1, lccn2))
    );

    var work = createResource(Map.of(), Set.of(WORK, BOOKS), Map.of(SUBJECT, List.of(concept)));
    return createResource(Map.of(), Set.of(INSTANCE), Map.of(INSTANTIATES, List.of(work)));
  }
}
