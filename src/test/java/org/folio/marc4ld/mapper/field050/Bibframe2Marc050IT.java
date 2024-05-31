package org.folio.marc4ld.mapper.field050;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.mapper.field050.Marc2Bibframe050IT.createLcClassification;
import static org.folio.marc4ld.mapper.field050.Marc2Bibframe050IT.createStatus;
import static org.folio.marc4ld.mapper.field082.Bibframe2Marc082IT.createResource;
import static org.folio.marc4ld.mapper.field082.Marc2Bibframe082IT.createAssigningSource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.util.Constants.Classification.DLC;
import static org.folio.marc4ld.util.Constants.Classification.NUBA;
import static org.folio.marc4ld.util.Constants.Classification.UBA;

import java.util.stream.Stream;
import org.folio.ld.dictionary.model.Resource;
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
class Bibframe2Marc050IT {

  @Autowired
  private Bibframe2MarcMapperImpl bibframe2MarcMapper;

  private static Stream<Arguments> provideArguments() {
    return Stream.of(
      Arguments.of(
        "fields/050/used_by_assigner_with_lc_source.jsonl",
        createResource(createLcClassification(createAssigningSource(DLC), createStatus(UBA)))
      ),
      Arguments.of(
        "fields/050/not_used_by_assigner_with_other_source.jsonl",
        createResource(createLcClassification(createAssigningSource(null), createStatus(NUBA)))
      ),
      Arguments.of(
        "fields/050/no_usage_information_with_lc_source.jsonl",
        createResource(createLcClassification(createAssigningSource(DLC), null))
      )
    );
  }

  @ParameterizedTest
  @MethodSource("provideArguments")
  void shouldMapLcClassificationCorrectly(String marcFile, Resource resource) {
    //given
    var expectedMarc = loadResourceAsString(marcFile);

    //when
    var result = bibframe2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }
}
