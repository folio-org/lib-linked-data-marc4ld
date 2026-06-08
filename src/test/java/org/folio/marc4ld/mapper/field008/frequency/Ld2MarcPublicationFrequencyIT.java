package org.folio.marc4ld.mapper.field008.frequency;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ILLUSTRATIONS;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.PUBLICATION_FREQUENCY;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONTINUING_RESOURCES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FREQUENCY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createCategory;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createCategorySet;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2MarcPublicationFrequencyIT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMap_publicationFrequency_whenWorkIsSerial() {
    // given
    var expectedMarc = """
      {
        "leader" : "00078n s a2200037uc 4500",
        "fields" : [ {
          "008" : "                  ax                   "
        } ]
      }""";
    var resource = createInstanceWithPublicationFrequencyAndWork(CONTINUING_RESOURCES);

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  @Test
  void shouldNotMap_publicationFrequency_whenWorkIsMonograph() {
    // given
    var expectedMarc = """
      {
        "leader" : "00078nam a2200037uc 4500",
        "fields" : [ {
          "008" : "                                       "
        } ]
      }""";
    var resource = createInstanceWithPublicationFrequencyAndWork(BOOKS);

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  @Test
  void shouldMap_publicationFrequency_whenInstanceHasBothBooksAndSerialFields_andIsSerial() {
    // given
    var expectedMarc = loadResourceAsString("fields/008/marc_008_publication_frequency.jsonl");
    var resource = createInstanceWithBothBooksAndSerialFields(CONTINUING_RESOURCES);

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  @Test
  void shouldNotMap_publicationFrequency_whenInstanceHasBothBooksAndSerialFields_andIsBook() {
    // given
    var expectedMarc = loadResourceAsString("fields/008/marc_008_illustrations.jsonl");
    var resource = createInstanceWithBothBooksAndSerialFields(BOOKS);

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithPublicationFrequencyAndWork(ResourceTypeDictionary workType) {
    var frequency18 = createResource(
      Map.of(
        CODE, List.of("a"),
        LINK, List.of("http://id.loc.gov/vocabulary/frequencies/ann"),
        LABEL, List.of("annual")
      ),
      Set.of(FREQUENCY),
      emptyMap()
    );
    var frequency19 = createResource(
      Map.of(
        CODE, List.of("x"),
        LINK, List.of("http://id.loc.gov/vocabulary/frequencies/irr"),
        LABEL, List.of("irregular")
      ),
      Set.of(FREQUENCY),
      emptyMap()
    );
    var work = createResource(
      emptyMap(),
      Set.of(WORK, workType),
      emptyMap()
    );
    return createResource(
      emptyMap(),
      Set.of(INSTANCE),
      Map.of(
        PUBLICATION_FREQUENCY, List.of(frequency18, frequency19),
        INSTANTIATES, List.of(work)
      )
    );
  }

  private Resource createInstanceWithBothBooksAndSerialFields(ResourceTypeDictionary workType) {
    var frequency18 = createResource(
      Map.of(
        CODE, List.of("a"),
        LINK, List.of("http://id.loc.gov/vocabulary/frequencies/ann"),
        LABEL, List.of("annual")
      ),
      Set.of(FREQUENCY),
      emptyMap()
    );
    var frequency19 = createResource(
      Map.of(
        CODE, List.of("x"),
        LINK, List.of("http://id.loc.gov/vocabulary/frequencies/irr"),
        LABEL, List.of("irregular")
      ),
      Set.of(FREQUENCY),
      emptyMap()
    );
    var illustrationsCategorySet = createCategorySet("http://id.loc.gov/vocabulary/millus", "Illustrative Content");
    var work = createResource(
      emptyMap(),
      Set.of(WORK, workType),
      Map.of(ILLUSTRATIONS, List.of(
        createCategory("b", "http://id.loc.gov/vocabulary/millus/map", "Maps", illustrationsCategorySet),
        createCategory("c", "http://id.loc.gov/vocabulary/millus/por", "Portraits", illustrationsCategorySet),
        createCategory("d", "http://id.loc.gov/vocabulary/millus/chr", "Charts", illustrationsCategorySet),
        createCategory("e", "http://id.loc.gov/vocabulary/millus/pln", "Plans", illustrationsCategorySet),
        createCategory("f", "http://id.loc.gov/vocabulary/millus/plt", "Plates", illustrationsCategorySet)
      ))
    );
    return createResource(
      emptyMap(),
      Set.of(INSTANCE),
      Map.of(
        PUBLICATION_FREQUENCY, List.of(frequency18, frequency19),
        INSTANTIATES, List.of(work)
      )
    );
  }
}
