package org.folio.marc4ld.authority.field100;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FAMILY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FORM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TEMPORAL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TOPIC;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.mapper.test.TestUtil;
import org.folio.marc4ld.service.marc2ld.Marc2BibframeMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class MarcToBibframeAuthority100IT {

  @Autowired
  private Marc2BibframeMapperImpl marc2BibframeMapper;

  @Test
  void shouldMapField100() {
    // given
    var marc = loadResourceAsString("authority/100/marc_100_concept.jsonl");

    //when
    var result = marc2BibframeMapper.fromMarcJson(marc);

    //then
    assertThat(result)
      .isNotNull()
      .satisfies(resource -> validateRootResource(resource, List.of(CONCEPT, PERSON)))
      .satisfies(resource -> validateFocus(resource, PERSON))
      .satisfies(this::validateForm)
      .satisfies(this::validateTopic)
      .satisfies(this::validateTemporal)
      .satisfies(this::validatePlace)
      .satisfies(this::validateIdentifier);
  }

  @Test
  void shouldMapField100Ind1Equals3() {
    // given
    var marc = loadResourceAsString("authority/100/marc_100_conceptInd1equals3.jsonl");

    //when
    var result = marc2BibframeMapper.fromMarcJson(marc);

    //then
    assertThat(result)
      .isNotNull()
      .satisfies(resource -> validateRootResource(resource, List.of(CONCEPT, FAMILY)))
      .satisfies(resource -> validateFocus(resource, FAMILY))
      .satisfies(this::validateForm)
      .satisfies(this::validateTopic)
      .satisfies(this::validateTemporal)
      .satisfies(this::validatePlace)
      .satisfies(this::validateIdentifier);
  }

  private void validateRootResource(Resource resource, List<ResourceTypeDictionary> types) {
    TestUtil.validateResource(resource, types,
      Map.ofEntries(
        Map.entry("http://bibfra.me/vocab/lite/name", "aValue"),
        Map.entry("http://bibfra.me/vocab/marc/numeration", "bValue"),
        Map.entry("http://bibfra.me/vocab/marc/titles", "cValue1, cValue2"), //TODO add array opportunities to test util
        Map.entry("http://bibfra.me/vocab/lite/date", "dValue"),
        Map.entry("http://bibfra.me/vocab/marc/attribution", "jValue1, jValue2"),
        Map.entry("http://bibfra.me/vocab/lite/nameAlternative", "qValue"),
        Map.entry("http://bibfra.me/vocab/marc/formSubdivision", "vValue1, vValue2"),
        Map.entry("http://bibfra.me/vocab/marc/generalSubdivision", "xValue1, xValue2"),
        Map.entry("http://bibfra.me/vocab/marc/chronologicalSubdivision", "yValue1, yValue2"),
        Map.entry("http://bibfra.me/vocab/marc/geographicSubdivision", "zValue1, zValue2"),
        Map.entry("http://library.link/vocab/resourcePreferred", "true"),
        Map.entry("http://bibfra.me/vocab/lite/label", "bValue, aValue, cValue1, cValue2, qValue, dValue" +
          " -- vValue1 -- vValue2, -- xValue1 -- xValue2 -- yValue1 -- yValue2 -- zValue1 -- zValue2")
      ),
      "bValue, aValue, cValue1, cValue2, qValue, dValue" +
        " -- vValue1 -- vValue2, -- xValue1 -- xValue2 -- yValue1 -- yValue2 -- zValue1 -- zValue2");
  }

  private void validateFocus(Resource resource, ResourceTypeDictionary resourceType) {
    var edge = getEdge(resource, resourceType);
    assertThat(edge)
      .isPresent();
    validateEdge(edge.get(), FOCUS, List.of(resourceType),
      Map.of(
        "http://bibfra.me/vocab/lite/name", "aValue",
        "http://bibfra.me/vocab/marc/numeration", "bValue",
        "http://bibfra.me/vocab/marc/titles", "cValue1, cValue2",
        "http://bibfra.me/vocab/lite/date", "dValue",
        "http://bibfra.me/vocab/marc/attribution", "jValue1, jValue2",
        "http://bibfra.me/vocab/lite/nameAlternative", "qValue"
      ),
      "bValue, aValue, cValue1, cValue2, qValue, dValue");
  }

  private void validateForm(Resource resource) {
    var edge = getEdge(resource, FORM);
    assertThat(edge)
      .isPresent();
    validateEdge(edge.get(), SUB_FOCUS, List.of(FORM),
      Map.of(
        "http://bibfra.me/vocab/marc/formSubdivision", "vValue1, vValue2",
        "http://bibfra.me/vocab/lite/label ", "vValue1, vValue2"
      ),
      "vValue1, vValue2");
  }

  private void validateTopic(Resource resource) {
    var edge = getEdge(resource, TOPIC);
    assertThat(edge)
      .isPresent();
    validateEdge(edge.get(), SUB_FOCUS, List.of(TOPIC),
      Map.of(
        "http://bibfra.me/vocab/lite/name", "xValue1, xValue2",
        "http://bibfra.me/vocab/lite/label ", "xValue1, xValue2"
      ),
      "xValue1, xValue2");
  }

  private void validateTemporal(Resource resource) {
    var edge = getEdge(resource, TEMPORAL);
    assertThat(edge)
      .isPresent();
    validateEdge(edge.get(), SUB_FOCUS, List.of(TEMPORAL),
      Map.of(
        "http://bibfra.me/vocab/lite/name", "yValue1, yValue2",
        "http://bibfra.me/vocab/lite/label ", "yValue1, yValue2"
      ),
      "yValue1, yValue2");
  }

  private void validatePlace(Resource resource) {
    var edge = getEdge(resource, PLACE);
    assertThat(edge)
      .isPresent();
    validateEdge(edge.get(), SUB_FOCUS, List.of(PLACE),
      Map.of(
        "http://bibfra.me/vocab/lite/name", "zValue1, zValue2",
        "http://bibfra.me/vocab/lite/label ", "zValue1, zValue2"
      ),
      "zValue1, zValue2");
  }


  //TODO asc what is 001
  private void validateIdentifier(Resource resource) {
    var edge = getEdge(resource, ID_LCCN, IDENTIFIER);
    assertThat(edge)
      .isPresent();
    validateEdge(edge.get(), SUB_FOCUS, List.of(ID_LCCN, IDENTIFIER),
      Map.of(
        "http://bibfra.me/vocab/lite/name", "001",
        "http://bibfra.me/vocab/lite/link", "http://id.loc.gov/authorities/001",
        "http://bibfra.me/vocab/lite/label", "001"
      ),
      "001");
  }

  private Optional<ResourceEdge> getEdge(Resource resource, ResourceTypeDictionary... resourceTypes) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(edge -> Optional.of(edge.getTarget())
        .map(Resource::getTypes)
        .filter(types -> CollectionUtils.containsAll(types, Arrays.asList(resourceTypes)))
        .isPresent())
      .findFirst();
  }
}
