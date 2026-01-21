package org.folio.marc4ld.mapper.field100and700;

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
import static org.folio.ld.dictionary.PropertyDictionary.MISC_INFO;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.NAME_ALTERNATIVE;
import static org.folio.ld.dictionary.PropertyDictionary.NUMBER_OF_PARTS;
import static org.folio.ld.dictionary.PropertyDictionary.NUMERATION;
import static org.folio.ld.dictionary.PropertyDictionary.TITLES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_FAST;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LOCAL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.AssertionsForClassTypes;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.FolioMetadata;
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
class LdToMarc100And700IT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @ParameterizedTest
  @CsvSource(value = {
    "PERSON, CREATOR, fields/100_700/marc_100_person.jsonl",
    "PERSON, CONTRIBUTOR, fields/100_700/marc_700_person.jsonl",
    "FAMILY, CREATOR, fields/100_700/marc_100_family.jsonl",
    "FAMILY, CONTRIBUTOR, fields/100_700/marc_700_family.jsonl"
  })
  void shouldMapField100And700(ResourceTypeDictionary type, PredicateDictionary predicate, String marcFile) {
    //given
    var resource = createResourceWithWork(type, predicate);
    var expectedMarc = loadResourceAsString(marcFile);

    //when
    var result = ld2MarcMapper.toMarcJson(resource);

    //then
    AssertionsForClassTypes.assertThat(result)
      .isEqualTo(expectedMarc);
  }

  private Resource createResourceWithWork(ResourceTypeDictionary type, PredicateDictionary predicate) {
    var lccnId = createResource(
      Map.of(LINK, List.of("http://id.loc.gov/authorities/names/n1234567890")),
      Set.of(IDENTIFIER, ID_LCCN),
      emptyMap()
    ).setLabel("n1234567890");

    var fastId = createResource(
      Map.of(LINK, List.of("http://id.worldcat.org/fast/fst1234567890")),
      Set.of(IDENTIFIER, ID_FAST),
      emptyMap()
    ).setLabel("fst1234567890");

    var localId = createResource(
      Map.of(NAME, List.of("1234567890")),
      Set.of(IDENTIFIER, ID_LOCAL),
      emptyMap()
    ).setLabel("1234567890");

    var creator = createResource(
        Map.ofEntries(
          entry(AUTHORITY_LINK, List.of("(FAST)fst1234567890", "http://id.worldcat.org/fast/fst1234567890")),
          entry(EQUIVALENT, List.of("equivalent", "another equivalent")),
          entry(LINKAGE, List.of("linkage")),
          entry(CONTROL_FIELD, List.of("control field", "another control field")),
          entry(FIELD_LINK, List.of("field link", "another field link")),
          entry(NAME, List.of("name")),
          entry(NUMERATION, List.of("numeration")),
          entry(TITLES, List.of("titles", "another titles")),
          entry(DATE, List.of("date")),
          entry(MISC_INFO, List.of("misc info", "another misc info")),
          entry(NUMBER_OF_PARTS, List.of("number of parts", "another number of parts")),
          entry(ATTRIBUTION, List.of("attribution", "another attribution")),
          entry(NAME_ALTERNATIVE, List.of("name alternative")),
          entry(AFFILIATION, List.of("affiliation"))
        ),
        Set.of(type),
        Map.of(PredicateDictionary.MAP, List.of(localId, lccnId, fastId))
      )
      .setLabel("name")
      .setFolioMetadata(new FolioMetadata().setInventoryId("8473ef4b-001f-46b3-a60e-52bcdeb3d5b2"));

    var work = createResource(
      Collections.emptyMap(),
      Set.of(WORK, BOOKS),
      Map.of(
        predicate, List.of(creator),
        PredicateDictionary.AUTHOR, List.of(creator)
      )
    ).setLabel("Work: label");

    return createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      Map.of(PredicateDictionary.INSTANTIATES, List.of(work))
    );
  }
}
