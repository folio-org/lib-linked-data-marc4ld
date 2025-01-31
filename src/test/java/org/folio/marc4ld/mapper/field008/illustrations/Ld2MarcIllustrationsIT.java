package org.folio.marc4ld.mapper.field008.illustrations;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ILLUSTRATIONS;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createCategory;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createCategorySet;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.model.Resource;
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
class Ld2MarcIllustrationsIT {

  @Autowired
  @Qualifier(Ld2MarcUnitedMapper.NAME)
  private Ld2MarcMapper ld2MarcMapper;

  @Test
  void shouldMap_onlyFour_illustrations() {
    // given
    var expectedMarc = loadResourceAsString("fields/008/marc_008_illustrations.jsonl");
    var resource = createInstance();

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createInstance() {
    var categorySet = createCategorySet("http://id.loc.gov/vocabulary/millus", "Illustrative Content");
    var work = createResource(
      emptyMap(),
      Set.of(WORK),
      Map.of(ILLUSTRATIONS, List.of(
        createCategory("b", "http://id.loc.gov/vocabulary/millus/map", "Maps", categorySet),
        createCategory("c", "http://id.loc.gov/vocabulary/millus/por", "Portraits", categorySet),
        createCategory("d", "http://id.loc.gov/vocabulary/millus/chr", "Charts", categorySet),
        createCategory("e", "http://id.loc.gov/vocabulary/millus/pln", "Plans", categorySet),
        createCategory("f", "http://id.loc.gov/vocabulary/millus/plt", "Plates", categorySet)
      ))
    );

    return createResource(
      emptyMap(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }
}
