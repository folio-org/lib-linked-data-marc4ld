package org.folio.marc4ld.mapper;

import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PropertyDictionary.DATE_START;
import static org.folio.ld.dictionary.PropertyDictionary.DIMENSIONS;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONTINUING_RESOURCES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.JSON_MAPPER;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

  @ParameterizedTest
  @MethodSource("workTypes")
  void shouldSetDescriptiveCatalogingFormToC_whenConvertingToMarc(ResourceTypeDictionary workType) {
    // given
    var instance = createInstanceWithWork(workType);

    // when
    var result = ld2MarcMapper.toMarcJson(instance);

    // then
    var leader = JSON_MAPPER.readTree(result).get("leader").asString();
    assertThat(leader).hasSizeGreaterThan(18);
    assertThat(leader.charAt(18)).isEqualTo('c');
  }

  private static Stream<ResourceTypeDictionary> workTypes() {
    return Stream.of(BOOKS, CONTINUING_RESOURCES);
  }

  @Test
  void shouldApply1xxPrecedenceWhenMixedCreatorTypesArePresent() {
    // given: a work with a PERSON creator (→ 100) and an ORGANIZATION creator (→ 110).
    // The Marc1xxTo7xxNormalizer must keep exactly one 1XX main entry: 100 takes priority over 110.
    var person = createResource(
        Map.of(NAME, List.of("Person Author")),
        Set.of(PERSON),
        Collections.emptyMap()
      ).setLabel("Person Author");

    var organization = createResource(
        Map.of(NAME, List.of("Organization Author")),
        Set.of(ORGANIZATION),
        Collections.emptyMap()
      ).setLabel("Organization Author");

    var work = createResource(
      Collections.emptyMap(),
      Set.of(WORK, BOOKS),
      Map.of(CREATOR, List.of(person, organization))
    );

    var resource = createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then: 100 is the sole main entry; 110 is converted to 710
    var fields = JSON_MAPPER.readTree(result).get("fields");
    assertThat(stream(fields.spliterator(), false).filter(f -> f.has("100")).toList()).hasSize(1);
    assertThat(stream(fields.spliterator(), false).filter(f -> f.has("110")).toList()).isEmpty();
    assertThat(stream(fields.spliterator(), false).filter(f -> f.has("710")).toList()).hasSize(1);
  }

  private Resource createInstanceWithWork(ResourceTypeDictionary workType) {
    var work = createResource(Map.of(), Set.of(WORK, workType), Map.of());
    return createResource(Map.of(), Set.of(INSTANCE), Map.of(INSTANTIATES, List.of(work)));
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
