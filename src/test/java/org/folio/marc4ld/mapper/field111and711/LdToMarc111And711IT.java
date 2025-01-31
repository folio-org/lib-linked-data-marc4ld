package org.folio.marc4ld.mapper.field111and711;

import static java.util.Collections.emptyMap;
import static java.util.Map.entry;
import static org.folio.ld.dictionary.PropertyDictionary.AFFILIATION;
import static org.folio.ld.dictionary.PropertyDictionary.AUTHORITY_LINK;
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
import static org.folio.ld.dictionary.ResourceTypeDictionary.MEETING;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.AssertionsForClassTypes;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.FolioMetadata;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.MonographTestUtil;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapper;
import org.folio.marc4ld.service.ld2marc.impl.Ld2MarcUnitedMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class LdToMarc111And711IT {

  @Autowired
  @Qualifier(Ld2MarcUnitedMapper.NAME)
  private Ld2MarcMapper ld2MarcMapper;

  @ParameterizedTest
  @CsvSource(value = {
    "CREATOR, fields/111_711/marc_111.jsonl",
    "CONTRIBUTOR, fields/111_711/marc_711.jsonl"
  })
  void shouldMapField111And711(PredicateDictionary predicate, String marcFile) {
    //given
    var resource = createResourceWithWork(predicate);
    var expectedMarc = loadResourceAsString(marcFile);

    //when
    var result = ld2MarcMapper.toMarcJson(resource);

    //then
    AssertionsForClassTypes.assertThat(result)
      .isEqualTo(expectedMarc);
  }

  private Resource createResourceWithWork(PredicateDictionary predicate) {
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

    var meeting = MonographTestUtil.createResource(
        Map.ofEntries(
          entry(NAME, List.of("name")),
          entry(AUTHORITY_LINK, List.of("authority link", "another authority link")),
          entry(SUBORDINATE_UNIT, List.of("subordinate unit", "another subordinate unit")),
          entry(PLACE, List.of("place", "another place")),
          entry(DATE, List.of("date", "another date")),
          entry(AFFILIATION, List.of("affiliation")),
          entry(EQUIVALENT, List.of("equivalent", "another equivalent")),
          entry(LINKAGE, List.of("linkage")),
          entry(CONTROL_FIELD, List.of("control field", "another control field")),
          entry(FIELD_LINK, List.of("field link", "another field link"))
        ),
        Set.of(MEETING),
        Map.of(PredicateDictionary.MAP, List.of(lccn, anotherLccn))
      )
      .setLabel("name")
      .setFolioMetadata(new FolioMetadata().setInventoryId("8473ef4b-001f-46b3-a60e-52bcdeb3d5b2"));

    var work = MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(WORK),
      Map.of(
        predicate, List.of(meeting),
        PredicateDictionary.AUTHOR, List.of(meeting)
      )
    ).setLabel("Work: label");

    return MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      Map.of(PredicateDictionary.INSTANTIATES, List.of(work))
    );
  }
}
