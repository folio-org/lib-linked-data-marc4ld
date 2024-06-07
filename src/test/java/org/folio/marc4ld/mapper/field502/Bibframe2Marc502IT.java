package org.folio.marc4ld.mapper.field502;

import static org.folio.ld.dictionary.PropertyDictionary.DEGREE;
import static org.folio.ld.dictionary.PropertyDictionary.DISSERTATION_ID;
import static org.folio.ld.dictionary.PropertyDictionary.DISSERTATION_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.DISSERTATION_YEAR;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.AssertionsForClassTypes;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
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
class Bibframe2Marc502IT {

  @Autowired
  private Bibframe2MarcMapperImpl bibframe2MarcMapper;

  @Test
  void shouldMapField502() {
    // given
    var expectedMarc = loadResourceAsString("fields/502/marc_502_multiple.jsonl");
    var resource = createResourceWith502();

    //when
    var result = bibframe2MarcMapper.toMarcJson(resource);

    // then
    AssertionsForClassTypes.assertThat(result)
      .isEqualTo(expectedMarc);
  }

  private Resource createResourceWith502() {
    var dissertation1 = createDissertation("1");
    var dissertation2 = createDissertation("2");

    var work = MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(WORK),
      Map.of(PredicateDictionary.DISSERTATION, List.of(dissertation1, dissertation2))
    ).setLabel("Work: label");

    return MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      Map.of(PredicateDictionary.INSTANTIATES, List.of(work))
    );
  }

  private static Resource createDissertation(String dataSuffix) {
    var organization = MonographTestUtil.createResource(
      Map.of(
        PropertyDictionary.NAME, List.of("dissertation granting institution " + dataSuffix)
      ),
      Set.of(ResourceTypeDictionary.ORGANIZATION),
      Map.of()
    );

    return MonographTestUtil.createResource(
      Map.of(
        LABEL, List.of("dissertation label " + dataSuffix),
        DEGREE, List.of("dissertation degree " + dataSuffix),
        DISSERTATION_YEAR, List.of("dissertation year " + dataSuffix),
        DISSERTATION_NOTE, List.of("dissertation note 1 " + dataSuffix, "dissertation note 2 " + dataSuffix),
        DISSERTATION_ID, List.of("dissertation ID 1 " + dataSuffix, "dissertation ID 2 " + dataSuffix)
      ),
      Set.of(ResourceTypeDictionary.DISSERTATION),
      Map.of(PredicateDictionary.GRANTING_INSTITUTION, List.of(organization))
    );
  }
}
