package org.folio.marc4ld.mapper.field245;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.NON_SORT_NUM;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.STATEMENT_OF_RESPONSIBILITY;
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
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapper;
import org.folio.marc4ld.service.ld2marc.impl.Ld2MarcUnitedMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2Marc245IT {

  @Autowired
  @Qualifier(Ld2MarcUnitedMapper.NAME)
  private Ld2MarcMapper ld2MarcMapper;

  @Test
  void whenCombine_map_shouldReturnCorrectlyMappedMarcJson() {
    // given
    var expectedMarc = loadResourceAsString("fields/245/marc_245_combine.jsonl");
    var resource = createResourceWithWorkWith245();

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result)
      .isEqualTo(expectedMarc);
  }

  @Test
  void whenSameValues_map_shouldReturnCorrectlyMappedMarcJson() {
    // given
    var expectedMarc = loadResourceAsString("fields/245/marc_245_similar.jsonl");
    var resource = createResourceWithSimilarFields245();

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result)
      .isEqualTo(expectedMarc);
  }

  @Test
  void whenTitleInBothInstanceAndWork_andMapOnlyInstance() {
    // given
    var expectedMarc = loadResourceAsString("fields/245/marc_245_title_conflict.jsonl");
    var resource = createResourceWithWorkAndInstanceSimilarTitleTypes();

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result)
      .isEqualTo(expectedMarc);
  }

  private Resource createResourceWithSimilarFields245() {
    var instanceTitle = MonographTestUtil.createResource(
      Map.of(
        PART_NAME, List.of("Test string", "Another test string"),
        MAIN_TITLE, List.of("Test string"),
        SUBTITLE, List.of("Test string")
      ),
      Set.of(ResourceTypeDictionary.TITLE),
      emptyMap()
    ).setLabel("Instance MainTitle");

    var outgoingResources = new LinkedHashMap<PredicateDictionary, List<Resource>>();
    outgoingResources.put(PredicateDictionary.TITLE, List.of(instanceTitle));

    return createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      outgoingResources
    );
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

    var outgoingResources = new LinkedHashMap<PredicateDictionary, List<Resource>>();
    outgoingResources.put(PredicateDictionary.TITLE, List.of(instanceTitle, instanceTitle2));

    return createResource(
      Map.of(
        STATEMENT_OF_RESPONSIBILITY, List.of("Statement Of Responsibility")
      ),
      Set.of(INSTANCE),
      outgoingResources
    );
  }

  private Resource createResourceWithWorkAndInstanceSimilarTitleTypes() {
    var workTitle = MonographTestUtil.createResource(
      Map.of(
        SUBTITLE, List.of("Work subtitle")
      ),
      Set.of(ResourceTypeDictionary.TITLE),
      emptyMap()
    ).setLabel("Work MainTitle");

    var outgoingWorkResources = new LinkedHashMap<PredicateDictionary, List<Resource>>();
    outgoingWorkResources.put(PredicateDictionary.TITLE, List.of(workTitle));

    var work = MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(WORK),
      outgoingWorkResources
    ).setLabel("Work: label");


    var instanceTitle = MonographTestUtil.createResource(
      Map.of(
        MAIN_TITLE, List.of("Instance title")
      ),
      Set.of(ResourceTypeDictionary.TITLE),
      emptyMap()
    ).setLabel("Instance MainTitle");

    var outgoingResources = new LinkedHashMap<PredicateDictionary, List<Resource>>();
    outgoingResources.put(PredicateDictionary.TITLE, List.of(instanceTitle));
    outgoingResources.put(PredicateDictionary.INSTANTIATES, List.of(work));

    return createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      outgoingResources
    );
  }
}
