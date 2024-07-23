package org.folio.marc4ld.mapper.field776;

import static java.util.Collections.emptyMap;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.map.HashedMap;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Bibframe2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Bibframe2Marc776IT {

  @Autowired
  private Bibframe2MarcMapperImpl bibframe2MarcMapper;

  @Test
  void map_shouldNotReturn776Field() {
    // given
    var expectedMarc = loadResourceAsString("fields/776/marc_776_skipped.jsonl");
    var instance = getSampleInstanceWithWork(getSampleWork());

    // when
    var result = bibframe2MarcMapper.toMarcJson(instance);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource getSampleInstanceWithWork(Resource work) {
    var instanceTitle = createResource(
      Map.of(
        MAIN_TITLE, List.of("MainTitle")
      ),
      Set.of(ResourceTypeDictionary.TITLE),
      emptyMap()
    ).setLabel("MainTitle");
    var pred2OutgoingResources = new LinkedHashMap<PredicateDictionary, List<Resource>>();
    pred2OutgoingResources.put(TITLE, List.of(instanceTitle));
    var instance = createResource(
      new HashedMap<>(),
      Set.of(INSTANCE),
      pred2OutgoingResources);
    if (nonNull(work)) {
      var edge = new ResourceEdge(instance, work, INSTANTIATES);
      instance.addOutgoingEdge(edge);
    }
    return instance;
  }

  private Resource getSampleWork() {
    var primaryTitle = createPrimaryTitle(null);
    var pred2OutgoingResources = new LinkedHashMap<PredicateDictionary, List<Resource>>();
    pred2OutgoingResources.put(TITLE, List.of(primaryTitle));
    var work = createResource(
      new HashedMap<>(),
      Set.of(WORK),
      pred2OutgoingResources
    );
    work.setLabel(primaryTitle.getLabel());
    return work;
  }

  private Resource createPrimaryTitle(Long id) {
    var primaryTitleValue = "Primary: mainTitle" + (nonNull(id) ? id : "");
    return createResource(
      Map.of(
        MAIN_TITLE, List.of(primaryTitleValue)
      ),
      Set.of(ResourceTypeDictionary.TITLE),
      emptyMap()
    ).setLabel(primaryTitleValue);
  }
}
