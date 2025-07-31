package org.folio.marc4ld.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PropertyDictionary.DATE_START;
import static org.folio.ld.dictionary.PropertyDictionary.DIMENSIONS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2MarcMappingIT {

  @Autowired
  private Ld2MarcMapper ld2MarcMapper;

  @Test
  void shouldNotMapEmptyProperties() {
    // given
    var expectedMarc = """
      {
        "leader" : "00078nam a2200037uc 4500",
        "fields" : [ {
          "008" : "                                       "
        } ]
      }""";
    var resource = createResourceWithEmptyProperties();

    //when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createResourceWithEmptyProperties() {
    var work = createResource(
      Map.of(DATE_START, List.of("")),
      Set.of(WORK, BOOKS),
      Map.of()
    );

    return createResource(
      Map.of(DIMENSIONS, List.of("")),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }
}
