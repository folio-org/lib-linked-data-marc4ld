package org.folio.marc4ld.mapper.field100;

import static java.util.Collections.emptyMap;
import static java.util.Map.entry;
import static org.folio.ld.dictionary.PropertyDictionary.AFFILIATION;
import static org.folio.ld.dictionary.PropertyDictionary.ATTRIBUTION;
import static org.folio.ld.dictionary.PropertyDictionary.AUTHORITY_LINK;
import static org.folio.ld.dictionary.PropertyDictionary.CONTROL_FIELD;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.EQUIVALENT;
import static org.folio.ld.dictionary.PropertyDictionary.FIELD_LINK;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.LINKAGE;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.NAME_ALTERNATIVE;
import static org.folio.ld.dictionary.PropertyDictionary.NUMERATION;
import static org.folio.ld.dictionary.PropertyDictionary.TITLES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.AssertionsForClassTypes;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.mapper.test.MonographTestUtil;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Bibframe2MarcMapperImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class BibframeToMarc100IT {

  @Autowired
  private Bibframe2MarcMapperImpl bibframe2MarcMapper;

  @ParameterizedTest
  @CsvSource(value = {
    "fields/100/marc_100_person.jsonl, PERSON",
    "fields/100/marc_100_family.jsonl, FAMILY"
  })
  void shouldMapField100(String marcFile, ResourceTypeDictionary type) {
    // given
    var expectedMarc = loadResourceAsString(marcFile);
    var resource = createResourceWithWorkWith100(type);

    //when
    var result = bibframe2MarcMapper.toMarcJson(resource);

    // then
    AssertionsForClassTypes.assertThat(result)
      .isEqualTo(expectedMarc);
  }

  private Resource createResourceWithWorkWith100(ResourceTypeDictionary type) {
    var lccn = MonographTestUtil.createResource(
      Map.ofEntries(
        entry(LINK, List.of("lccn link"))
      ),
      Set.of(IDENTIFIER, ID_LCCN),
      emptyMap()
    ).setLabel("lccn");

    var anotherLccn = MonographTestUtil.createResource(
      Map.ofEntries(
        entry(LINK, List.of("another lccn link"))
      ),
      Set.of(IDENTIFIER, ID_LCCN),
      emptyMap()
    ).setLabel("another lccn");

    var creator = MonographTestUtil.createResource(
      Map.ofEntries(
        entry(AUTHORITY_LINK, List.of("authority link", "another authority link")),
        entry(EQUIVALENT, List.of("equivalent", "another equivalent")),
        entry(LINKAGE, List.of("linkage")),
        entry(CONTROL_FIELD, List.of("control field", "another control field")),
        entry(FIELD_LINK, List.of("field link", "another field link")),
        entry(NAME, List.of("name")),
        entry(NUMERATION, List.of("numeration")),
        entry(TITLES, List.of("titles", "another titles")),
        entry(DATE, List.of("date")),
        entry(ATTRIBUTION, List.of("attribution", "another attribution")),
        entry(NAME_ALTERNATIVE, List.of("name alternative")),
        entry(AFFILIATION, List.of("affiliation"))
      ),
      Set.of(type),
      Map.of(PredicateDictionary.MAP, List.of(lccn, anotherLccn))
    ).setLabel("name");

    var work = MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(WORK),
      Map.of(
        PredicateDictionary.CREATOR, List.of(creator)
      )
    ).setLabel("Work: label");

    creator.getIncomingEdges().add(new ResourceEdge(work, creator, PredicateDictionary.AUTHOR));
    creator.getIncomingEdges().add(new ResourceEdge(work, creator, PredicateDictionary.DESIGNER));

    return MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      Map.of(PredicateDictionary.INSTANTIATES, List.of(work))
    );
  }
}
