package org.folio.marc4ld.mapper.field008;

import static org.folio.marc4ld.mapper.test.MonographTestUtil.getInstanceWithAdminMetadata;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.getLightWeightInstanceResource;
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
class Ld2Marc008IT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void map_shouldMapCreatedDateCorrectly() {
    // given
    var instanceWithAdminMetadata = getInstanceWithAdminMetadata("241219");
    var expectedMarc = loadResourceAsString("fields/008/marc_008_created_at_admin_metadata.jsonl");

    // when
    var actualMarc = ld2MarcMapper.toMarcJson(instanceWithAdminMetadata);

    // then
    assertEquals(expectedMarc, actualMarc);
  }

  @ParameterizedTest
  @MethodSource("createdDateSource")
   void map_shouldMapCreatedDateCorrectly_ifAdminMetadataNotContainsCreatedDate(Date createdDate, String marcFile) {
    // given
    var instance = getLightWeightInstanceResource();
    instance.setCreatedDate(createdDate);
    var expectedMarc = loadResourceAsString(marcFile);

    // when
    var actualMarc = ld2MarcMapper.toMarcJson(instance);

    // then
    assertEquals(expectedMarc, actualMarc);
  }

  private static Stream<Arguments> createdDateSource() {
    return Stream.of(
      arguments(new Date(1734000555555L), "fields/008/marc_008_created_at_resource.jsonl"),
      arguments(null, "fields/008/marc_008_created_at_empty.jsonl")
    );
  }
}
