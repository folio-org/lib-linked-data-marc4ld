package org.folio.marc4ld.mapper.field999;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.getLightWeightInstanceResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.stream.Stream;
import org.folio.ld.dictionary.model.FolioMetadata;
import org.folio.ld.dictionary.model.ResourceSource;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.service.ld2marc.Bibframe2MarcMapperImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

class Bibframe2Marc999IT extends Marc2LdTestBase {

  @Autowired
  private Bibframe2MarcMapperImpl bibframe2MarcMapper;

  @ParameterizedTest
  @MethodSource("provide999FieldArguments")
  void map_shouldNotReturnEmpty999Field(FolioMetadata folioMetadata, String resourceName) {
    // given
    var expectedMarc = loadResourceAsString(resourceName);
    var resource =  getLightWeightInstanceResource();
    resource.setFolioMetadata(folioMetadata);

    // when
    var actualMarc = bibframe2MarcMapper.toMarcJson(resource);

    // then
    assertThat(actualMarc).isEqualTo(expectedMarc);
  }

  static Stream<Arguments> provide999FieldArguments() {
    return Stream.of(
      Arguments.of(new FolioMetadata().setSource(ResourceSource.MARC),
        "fields/999/marc_field999_empty.jsonl"),
      Arguments.of(new FolioMetadata().setSource(ResourceSource.LINKED_DATA),
        "fields/999/marc_field999_empty.jsonl"),
      Arguments.of(new FolioMetadata()
          .setSource(ResourceSource.MARC)
          .setInventoryId("2165ef4b-001f-46b3-a60e-52bcdeb3d5a1"),
        "fields/999/marc_field999_inventory.jsonl"),
      Arguments.of(new FolioMetadata()
          .setSource(ResourceSource.MARC)
          .setSrsId("43d58061-decf-4d74-9747-0e1c368e861b"),
        "fields/999/marc_field999_srs.jsonl")
    );
  }
}
