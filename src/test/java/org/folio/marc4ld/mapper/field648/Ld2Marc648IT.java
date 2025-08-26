package org.folio.marc4ld.mapper.field648;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PropertyDictionary.CHRONOLOGICAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.CONTROL_FIELD;
import static org.folio.ld.dictionary.PropertyDictionary.EQUIVALENT;
import static org.folio.ld.dictionary.PropertyDictionary.FIELD_LINK;
import static org.folio.ld.dictionary.PropertyDictionary.FORM_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GENERAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.LINKAGE;
import static org.folio.ld.dictionary.PropertyDictionary.MATERIALS_SPECIFIED;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.RELATOR_CODE;
import static org.folio.ld.dictionary.PropertyDictionary.RELATOR_TERM;
import static org.folio.ld.dictionary.PropertyDictionary.SOURCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TEMPORAL;
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
class Ld2Marc648IT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;


  @Test
  void shouldMapField648() {
    // given
    var resource = createInstanceWithTemporalSubject();
    var expectedMarc = loadResourceAsString("fields/648/marc_648.jsonl");

    // when
    var actualMarc = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(actualMarc).isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithTemporalSubject() {

    var lccn = createResource(
      Map.of(
        NAME, List.of("n1234567890"),
        LINK, List.of("https://id.loc.gov/authorities/names/n1234567890")
      ),
      Set.of(IDENTIFIER, ID_LCCN),
      Map.of()
    );

    var concept = createResource(
      Map.ofEntries(
        Map.entry(NAME, List.of("1793")),
        Map.entry(RELATOR_TERM, List.of("setting.")),
        Map.entry(FORM_SUBDIVISION, List.of("form 1")),
        Map.entry(GENERAL_SUBDIVISION, List.of("topic 1", "topic 2")),
        Map.entry(GEOGRAPHIC_SUBDIVISION, List.of("place 1", "place 2")),
        Map.entry(CHRONOLOGICAL_SUBDIVISION, List.of("tmp 1", "tmp 2")),
        Map.entry(EQUIVALENT, List.of("equivalent")),
        Map.entry(SOURCE, List.of("source")),
        Map.entry(MATERIALS_SPECIFIED, List.of("materials specified")),
        Map.entry(RELATOR_CODE, List.of("relator code")),
        Map.entry(LINKAGE, List.of("linkage")),
        Map.entry(CONTROL_FIELD, List.of("control field")),
        Map.entry(FIELD_LINK, List.of("field link"))
      ),
      Set.of(TEMPORAL, CONCEPT),
      Map.of(FOCUS, List.of(), MAP, List.of(lccn))
    );

    var work = createResource(
      Map.of(),
      Set.of(WORK, BOOKS),
      Map.of(SUBJECT, List.of(concept))
    );
    return createResource(
      Map.of(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }
}
