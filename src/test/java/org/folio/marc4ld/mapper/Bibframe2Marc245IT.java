package org.folio.marc4ld.mapper;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.NON_SORT_NUM;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.RESPONSIBILITY_STATEMENT;
import static org.folio.ld.dictionary.PropertyDictionary.SUBTITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.MonographTestUtil;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Bibframe2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Bibframe2Marc245IT {

  @Autowired
  private Bibframe2MarcMapperImpl bibframe2MarcMapper;

  @Test
  void map_shouldReturnCorrectlyMappedMarcJson() {
    // given
    var expectedMarc = loadResourceAsString("fields/marc_245.jsonl");
    var resource = createResourceWithWorkWith245();

    // when
    var result = bibframe2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result)
      .isEqualTo(expectedMarc);
  }

  private Resource createResourceWithWorkWith245() {
    var instanceTitle = MonographTestUtil.createResource(
      Map.of(
        PART_NAME, List.of("Instance PartName 1"),
        PART_NUMBER, List.of("11"),
        MAIN_TITLE, List.of("Instance MainTitle"),
        NON_SORT_NUM, List.of("7"),
        SUBTITLE, List.of("Instance SubTitle")
      ),
      Set.of(ResourceTypeDictionary.TITLE),
      emptyMap()
    ).setLabel("Instance MainTitle");

    var instanceTitle2 = MonographTestUtil.createResource(
      Map.of(
        PART_NAME, List.of("Instance PartName 2"),
        PART_NUMBER, List.of("22"),
        MAIN_TITLE, List.of("Instance Title empty")
      ),
      Set.of(ResourceTypeDictionary.TITLE),
      emptyMap()
    ).setLabel("Instance Title empty");

    var work = MonographTestUtil.createResource(
      Map.of(
        RESPONSIBILITY_STATEMENT, List.of("Statement Of Responsibility")
      ),
      Set.of(WORK),
      Collections.emptyMap()
    ).setLabel("Work: label");

    var outgoingResources = new LinkedHashMap<PredicateDictionary, List<Resource>>();
    outgoingResources.put(PredicateDictionary.TITLE, List.of(instanceTitle, instanceTitle2));
    outgoingResources.put(PredicateDictionary.INSTANTIATES, List.of(work));

    return createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      outgoingResources
    );
  }
}
