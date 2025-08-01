package org.folio.marc4ld.mapper.field008.government.publication;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.GOVERNMENT_PUBLICATION;
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
    var resource = createInstanceWithWorkWithGovernmentPublication(workType);

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithWorkWithGovernmentPublication(ResourceTypeDictionary workType) {
    var categorySet = createCategorySet("http://id.loc.gov/vocabulary/mgovtpubtype", "Government Publication Type");
    var governmentPublication = createCategory("a", "http://id.loc.gov/vocabulary/mgovtpubtype/a", "Autonomous",
      categorySet);
    var work = createResource(
      emptyMap(),
      Set.of(WORK, workType),
      Map.of(GOVERNMENT_PUBLICATION, List.of(governmentPublication))
    );

    return createResource(
      emptyMap(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }

  private static Stream<Arguments> arguments() {
    return Stream.of(
      Arguments.of(BOOKS, "fields/008/marc_008_mgovtpubtype.jsonl"),
      Arguments.of(CONTINUING_RESOURCES, "fields/008/marc_008_mgovtpubtype_serial.jsonl")
    );
  }
}
