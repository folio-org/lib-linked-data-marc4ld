package org.folio.marc4ld.mapper.field110and710;

import static java.util.Collections.emptyMap;
import static java.util.Map.entry;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.folio.ld.dictionary.PropertyDictionary.AFFILIATION;
import static org.folio.ld.dictionary.PropertyDictionary.CONTROL_FIELD;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.EQUIVALENT;
import static org.folio.ld.dictionary.PropertyDictionary.FIELD_LINK;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.LINKAGE;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PLACE;
import static org.folio.ld.dictionary.PropertyDictionary.SUBORDINATE_UNIT;
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
import org.folio.ld.dictionary.model.FolioMetadata;
import org.folio.ld.dictionary.model.Resource;
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
class BibframeToMarc110and710IT {

  @Autowired
  private Bibframe2MarcMapperImpl bibframe2MarcMapper;

  @ParameterizedTest
  @CsvSource(value = {
    "ORGANIZATION, CREATOR, fields/110_710/marc_110_organization.jsonl",
    "JURISDICTION, CREATOR, fields/110_710/marc_110_jurisdiction.jsonl",
    "ORGANIZATION, CONTRIBUTOR, fields/110_710/marc_710_organization.jsonl",
    "JURISDICTION, CONTRIBUTOR, fields/110_710/marc_710_jurisdiction.jsonl"
  })
  void shouldMapField110_710(ResourceTypeDictionary type, PredicateDictionary predicate, String marcFile) {
    //given
    var resource = createResourceWithWorkWith110_710(type, predicate);
    var expectedMarc = loadResourceAsString(marcFile);

    //when
    var result = bibframe2MarcMapper.toMarcJson(resource);

    //then
    AssertionsForClassTypes.assertThat(result)
      .isEqualTo(expectedMarc);
  }

  private Resource createResourceWithWorkWith110_710(ResourceTypeDictionary type, PredicateDictionary predicate) {
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

    var authorOrganization = MonographTestUtil.createResource(
        Map.ofEntries(
          entry(NAME, List.of("author")),
          entry(SUBORDINATE_UNIT, List.of("subordinate unit", "another subordinate unit")),
          entry(PLACE, List.of("place", "another place")),
          entry(DATE, List.of("date", "another date")),
          entry(AFFILIATION, List.of("affiliation")),
          entry(EQUIVALENT, List.of("equivalent", "another equivalent")),
          entry(LINKAGE, List.of("linkage")),
          entry(CONTROL_FIELD, List.of("control field", "another control field")),
          entry(FIELD_LINK, List.of("field link", "another field link"))
        ),
        Set.of(type),
        Map.of(PredicateDictionary.MAP, List.of(lccn, anotherLccn))
      )
      .setLabel("author")
      .setFolioMetadata(new FolioMetadata().setInventoryId("8473ef4b-001f-46b3-a60e-52bcdeb3d5b2"));

    var editorOrganization = MonographTestUtil.createResource(
        Map.of(
          NAME, List.of("editor")
        ),
        Set.of(type),
        emptyMap()
      )
      .setLabel("editor")
      .setFolioMetadata(new FolioMetadata().setInventoryId(EMPTY));

    var work = MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(WORK),
      Map.of(
        predicate, List.of(authorOrganization, editorOrganization),
        PredicateDictionary.AUTHOR, List.of(authorOrganization),
        PredicateDictionary.EDITOR, List.of(editorOrganization)
      )
    ).setLabel("Work: label");

    return MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      Map.of(PredicateDictionary.INSTANTIATES, List.of(work))
    );
  }
}
