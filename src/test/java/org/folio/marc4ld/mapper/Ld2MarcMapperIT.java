package org.folio.marc4ld.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.getLightWeightInstanceResource;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.getSampleInstanceResource;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.getSampleWork;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Date;
import java.util.stream.Stream;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2MarcMapperIT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void map_shouldReturnCorrectlyMappedMarcJson() {
    // given
    var resource = getSampleInstanceResource();
    var expectedMarc = loadResourceAsString("expected_marc.jsonl");

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  @ParameterizedTest
  @MethodSource("dateFieldSource")
  void map_shouldMapDateFieldCorrectly(Date instanceUpdatedAt, Date workUpdatedAt, String expectedMarcFile) {
    // given
    var work = getSampleWork();
    work.setUpdatedAt(workUpdatedAt);
    var instance = getLightWeightInstanceResource(work);
    instance.setUpdatedAt(instanceUpdatedAt);
    var expectedMarc = loadResourceAsString(expectedMarcFile);

    // when
    var actualMarc = ld2MarcMapper.toMarcJson(instance);

    // then
    assertEquals(expectedMarc, actualMarc);
  }

  public static Stream<Arguments> dateFieldSource() {
    return Stream.of(
      arguments(new Date(1734000555555L), new Date(1734000500000L), "fields/005/marc_instance_date.jsonl"),
      arguments(new Date(1734000500000L), new Date(1734000666666L), "fields/005/marc_work_date.jsonl"),
      arguments(null, new Date(1734000555555L), "fields/005/marc_instance_date.jsonl"),
      arguments(new Date(1734000666666L), null, "fields/005/marc_work_date.jsonl"),
      arguments(null, null, "fields/005/marc_empty_date_field.jsonl")
    );
  }

}
