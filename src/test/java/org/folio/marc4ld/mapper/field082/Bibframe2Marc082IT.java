package org.folio.marc4ld.mapper.field082;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CLASSIFICATION;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.field082.Marc2Bibframe082IT.createAssigningSource;
import static org.folio.marc4ld.mapper.field082.Marc2Bibframe082IT.createDdcClassification;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.MonographTestUtil;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Bibframe2MarcMapperImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Bibframe2Marc082IT {

  @Autowired
  private Bibframe2MarcMapperImpl bibframe2MarcMapper;

  private static Stream<Arguments> provideArguments() {
    return Stream.of(
      Arguments.of(
        "fields/082/full_edition_with_lc_source.jsonl",
        createResource(
          createDdcClassification("Full", createAssigningSource("http://id.loc.gov/vocabulary/organizations/dlc"))
        )
      ),
      Arguments.of(
        "fields/082/abridged_edition_with_other_source.jsonl",
        createResource(
          createDdcClassification("Abridged", createAssigningSource(null))
        )
      ),
      Arguments.of(
        "fields/082/other_edition_without_source_info.jsonl",
        createResource(
          createDdcClassification(null, null)
        )
      )
    );
  }

  @ParameterizedTest
  @MethodSource("provideArguments")
  void shouldMapDdcClassificationCorrectly(String marcFile, Resource resource) {
    //given
    var expectedMarc = loadResourceAsString(marcFile);

    //when
    var result = bibframe2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private static Resource createResource(Resource ddcClassification) {
    var work = MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(WORK),
      Map.of(CLASSIFICATION, List.of(ddcClassification))
    );

    return MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }
}
