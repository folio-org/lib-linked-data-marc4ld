package org.folio.marc4ld.mapper.field008.government.publication;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CHARACTERISTIC;
import static org.folio.ld.dictionary.PredicateDictionary.GOVERNMENT_PUBLICATION;
import static org.folio.ld.dictionary.PredicateDictionary.ILLUSTRATIONS;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONTINUING_RESOURCES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createCategory;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createCategorySet;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
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
class Ld2MarcGovernmentPublicationIT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @ParameterizedTest
  @MethodSource("arguments")
  void shouldMap_governmentPublication_whenWorkIsBookOrSerial(ResourceTypeDictionary workType, String fileName) {
    // given
    var expectedMarc = loadResourceAsString(fileName);
    var resource = createInstance(workType, "a", "http://id.loc.gov/vocabulary/mgovtpubtype/a", "Autonomous");

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  @ParameterizedTest
  @MethodSource("argumentsForCodeO")
  void shouldMap_governmentPublication_whenCodeIsO(ResourceTypeDictionary workType, String fileName) {
    var expectedMarc = loadResourceAsString(fileName);
    var resource = createInstance(workType, "o",
        "http://id.loc.gov/vocabulary/mgovtpubtype/g", "Government");
    assertThat(ld2MarcMapper.toMarcJson(resource)).isEqualTo(expectedMarc);
  }

  @Test
  void shouldMap_governmentPublication_whenCodeIsU() {
    var expectedMarc = loadResourceAsString("fields/008/marc_008_mgovtpubtype_u.jsonl");
    var resource = createInstance(BOOKS, "u",
        "http://id.loc.gov/vocabulary/mgovtpubtype/u", "Unknown");
    assertThat(ld2MarcMapper.toMarcJson(resource)).isEqualTo(expectedMarc);
  }

  @Test
  void shouldMap_governmentPublication_whenCodeIsZ() {
    var expectedMarc = loadResourceAsString("fields/008/marc_008_mgovtpubtype_z.jsonl");
    var resource = createInstance(BOOKS, "z",
        "http://id.loc.gov/vocabulary/mgovtpubtype/z", "Other");
    assertThat(ld2MarcMapper.toMarcJson(resource)).isEqualTo(expectedMarc);
  }

  @Test
  void shouldMap_governmentPublication_andNotCharacteristic_whenWorkHasBothFields_andIsBook() {
    // given
    var expectedMarc = loadResourceAsString("fields/008/marc_008_mgovtpubtype.jsonl");
    var resource = createInstanceWithGovPubAndCharacteristic(BOOKS);

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  @Test
  void shouldMap_governmentPublication_andNotIllustrations_whenWorkHasBothFields_andIsSerial() {
    // given
    var expectedMarc = loadResourceAsString("fields/008/marc_008_mgovtpubtype_serial.jsonl");
    var resource = createInstanceWithGovPubAndIllustrations(CONTINUING_RESOURCES);

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createInstance(ResourceTypeDictionary workType,
                                  String code, String link, String term) {
    var categorySet = createCategorySet("http://id.loc.gov/vocabulary/mgovtpubtype", "Government Publication Type");
    var govPub = createCategory(code, link, term, categorySet);
    var work = createResource(emptyMap(), Set.of(WORK, workType),
        Map.of(GOVERNMENT_PUBLICATION, List.of(govPub)));
    return createResource(emptyMap(), Set.of(INSTANCE),
        Map.of(INSTANTIATES, List.of(work)));
  }

  private Resource createInstanceWithGovPubAndCharacteristic(ResourceTypeDictionary workType) {
    var govPubCategorySet =
      createCategorySet("http://id.loc.gov/vocabulary/mgovtpubtype", "Government Publication Type");
    var characteristicCategorySet =
      createCategorySet("http://id.loc.gov/vocabulary/mserialpubtype", "Serial Publication Type");
    var work = createResource(
      emptyMap(),
      Set.of(WORK, workType),
      Map.of(
        GOVERNMENT_PUBLICATION, List.of(
          createCategory("a", "http://id.loc.gov/vocabulary/mgovtpubtype/a", "Autonomous", govPubCategorySet)
        ),
        CHARACTERISTIC, List.of(
          createCategory("g", "http://id.loc.gov/vocabulary/mserialpubtype/mag", "magazine",
            characteristicCategorySet)
        )
      )
    );
    return createResource(emptyMap(), Set.of(INSTANCE), Map.of(INSTANTIATES, List.of(work)));
  }

  private Resource createInstanceWithGovPubAndIllustrations(ResourceTypeDictionary workType) {
    var govPubCategorySet =
      createCategorySet("http://id.loc.gov/vocabulary/mgovtpubtype", "Government Publication Type");
    var illustrationsCategorySet = createCategorySet("http://id.loc.gov/vocabulary/millus", "Illustrative Content");
    var work = createResource(
      emptyMap(),
      Set.of(WORK, workType),
      Map.of(
        GOVERNMENT_PUBLICATION, List.of(
          createCategory("a", "http://id.loc.gov/vocabulary/mgovtpubtype/a", "Autonomous", govPubCategorySet)
        ),
        ILLUSTRATIONS, List.of(
          createCategory("b", "http://id.loc.gov/vocabulary/millus/map", "Maps", illustrationsCategorySet),
          createCategory("c", "http://id.loc.gov/vocabulary/millus/por", "Portraits", illustrationsCategorySet),
          createCategory("d", "http://id.loc.gov/vocabulary/millus/chr", "Charts", illustrationsCategorySet),
          createCategory("e", "http://id.loc.gov/vocabulary/millus/pln", "Plans", illustrationsCategorySet),
          createCategory("f", "http://id.loc.gov/vocabulary/millus/plt", "Plates", illustrationsCategorySet)
        )
      )
    );
    return createResource(emptyMap(), Set.of(INSTANCE), Map.of(INSTANTIATES, List.of(work)));
  }

  private static Stream<Arguments> arguments() {
    return Stream.of(
      Arguments.of(BOOKS, "fields/008/marc_008_mgovtpubtype.jsonl"),
      Arguments.of(CONTINUING_RESOURCES, "fields/008/marc_008_mgovtpubtype_serial.jsonl")
    );
  }

  private static Stream<Arguments> argumentsForCodeO() {
    return Stream.of(
      Arguments.of(BOOKS,               "fields/008/marc_008_mgovtpubtype_o.jsonl"),
      Arguments.of(CONTINUING_RESOURCES, "fields/008/marc_008_mgovtpubtype_o_serial.jsonl")
    );
  }
}
