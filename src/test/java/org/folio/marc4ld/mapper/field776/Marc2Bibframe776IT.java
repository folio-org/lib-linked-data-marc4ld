package org.folio.marc4ld.mapper.field776;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.EXTENT;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.OTHER_VERSION;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.AGENT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_CODEN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_ISBN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_ISSN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LOCAL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_STRN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_UNKNOWN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Marc2Bibframe776IT extends Marc2LdTestBase {

  @Test
  void shouldMapField776Correctly() {
    //given
    var marc = loadResourceAsString("fields/776/marc_776.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .satisfies(resource -> validateLiteWork(resource, "work title main title"))
      .satisfies(resource -> validateAgent(resource, "agent name", "agent name 2"))
      .satisfies(this::validateIssn)
      .satisfies(resource -> validateLiteWorkTitle(resource, "work title main title"))
      .satisfies(resource -> validateLiteInstance(resource, 11, "work title main title"))
      .satisfies(this::validateExtent)
      .satisfies(resource -> validateLocalId(resource, "local ID identifier name", "local ID identifier name 2"))
      .satisfies(this::validateStrn)
      .satisfies(this::validateCoden)
      .satisfies(resource -> validateIsbn(resource, "ISBN identifier name", "ISBN identifier name 2"))
      .satisfies(this::validateUnknown)
      .satisfies(this::validateLccn)
      .satisfies(this::validateLiteInstanceTitle);
  }

  @Test
  void shouldMapField776WithMissingSubfieldsCorrectly() {
    //given
    var marc = loadResourceAsString("fields/776/marc_776_with_missing_subfields.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .satisfies(resource -> validateLiteWork(resource, "work/instance title main title"))
      .satisfies(resource -> validateAgent(resource, "agent name"))
      .satisfies(resource -> validateLiteWorkTitle(resource, "work/instance title main title"))
      .satisfies(resource -> validateLiteInstance(resource, 5, "work/instance title main title"))
      .satisfies(resource -> validateLocalId(resource, "local ID identifier name"))
      .satisfies(resource -> validateIsbn(resource, "ISBN identifier name"))
      .satisfies(this::validateUnknown)
      .satisfies(this::validateLiteInstanceTitle);
  }

  private void validateLiteWork(Resource resource, String expectedLabel) {
    var resourceEdges = getEdges(getWork(resource), WORK);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.get(0), OTHER_VERSION,
      List.of(WORK),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of(expectedLabel)
      ), expectedLabel);
  }

  private List<ResourceEdge> getEdges(Resource resource, ResourceTypeDictionary... resourceTypes) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(edge -> Optional.of(edge.getTarget())
        .map(Resource::getTypes)
        .filter(types -> CollectionUtils.containsAll(types, Arrays.asList(resourceTypes)))
        .isPresent())
      .toList();
  }

  private Resource getWork(Resource resource) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> INSTANTIATES == resourceEdge.getPredicate())
      .map(ResourceEdge::getTarget)
      .findFirst()
      .orElseThrow();
  }

  private void validateAgent(Resource resource, String... expectedNames) {
    var resourceEdges = getEdges(getLiteWork(resource), AGENT);
    assertThat(resourceEdges).hasSize(expectedNames.length);
    var index = 0;
    for (String expectedName : expectedNames) {
      validateEdge(resourceEdges.get(index), CREATOR,
        List.of(AGENT),
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of(expectedName)
        ), expectedName);
      index++;
    }
  }

  private Resource getLiteWork(Resource resource) {
    return getWork(resource)
      .getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> OTHER_VERSION == resourceEdge.getPredicate())
      .map(ResourceEdge::getTarget)
      .findFirst()
      .orElseThrow();
  }

  private void validateIssn(Resource resource) {
    var resourceEdges = getEdges(getLiteWork(resource), IDENTIFIER, ID_ISSN);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.get(0), MAP,
      List.of(IDENTIFIER, ID_ISSN),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("ISSN identifier name")
      ), "ISSN identifier name");
  }

  private void validateLiteWorkTitle(Resource resource, String expectedMainTitle) {
    var resourceEdges = getEdges(getLiteWork(resource), ResourceTypeDictionary.TITLE);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.get(0), TITLE,
      List.of(ResourceTypeDictionary.TITLE),
      Map.of(
        "http://bibfra.me/vocab/marc/mainTitle", List.of(expectedMainTitle),
        "http://bibfra.me/vocab/marc/qualifier", List.of("work title qualifier")
      ), expectedMainTitle);
  }

  private void validateLiteInstance(Resource resource, int expectedEdgesSize, String expectedWorkLabel) {
    var liteInstance = getLiteInstance(resource);
    validateResource(liteInstance, List.of(INSTANCE),
      Map.of(
        "http://bibfra.me/vocab/marc/edition", List.of("instance edition"),
        "http://bibfra.me/vocab/marc/publicationInformation", List.of("instance publication information"),
        "http://bibfra.me/vocab/marc/materials", List.of("instance materials"),
        "http://bibfra.me/vocab/lite/note", List.of("instance note"),
        "http://bibfra.me/vocab/marc/seriesStatement", List.of("instance series statement"),
        "http://bibfra.me/vocab/marc/reportNumber", List.of("instance report number")
      ), "work/instance title main title");
    assertThat(liteInstance.getOutgoingEdges()).hasSize(expectedEdgesSize);
    validateAllIds(liteInstance);
    var resourceEdges = getEdges(liteInstance, WORK);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.get(0), INSTANTIATES,
      List.of(WORK),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of(expectedWorkLabel)
      ), expectedWorkLabel);
  }

  private Resource getLiteInstance(Resource resource) {
    return getLiteWork(resource)
      .getIncomingEdges()
      .stream()
      .filter(resourceEdge -> INSTANTIATES == resourceEdge.getPredicate())
      .map(ResourceEdge::getSource)
      .findFirst()
      .orElseThrow();
  }

  private void validateExtent(Resource resource) {
    var resourceEdges = getEdges(getLiteInstance(resource), ResourceTypeDictionary.EXTENT);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.get(0), EXTENT,
      List.of(ResourceTypeDictionary.EXTENT),
      Map.of(
        "http://bibfra.me/vocab/lite/extent", List.of("extent")
      ), "extent");
  }

  private void validateLocalId(Resource resource, String... expectedLocalIds) {
    var resourceEdges = getEdges(getLiteInstance(resource), IDENTIFIER, ID_LOCAL);
    assertThat(resourceEdges).hasSize(expectedLocalIds.length);
    var index = 0;
    for (String expectedLocalId : expectedLocalIds) {
      validateEdge(resourceEdges.get(index), MAP,
        List.of(IDENTIFIER, ID_LOCAL),
        Map.of(
          "http://bibfra.me/vocab/marc/localId", List.of(expectedLocalId)
        ), expectedLocalId);
      index++;
    }
  }

  private void validateStrn(Resource resource) {
    var resourceEdges = getEdges(getLiteInstance(resource), IDENTIFIER, ID_STRN);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.get(0), MAP,
      List.of(IDENTIFIER, ID_STRN),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("STRN identifier name")
      ), "STRN identifier name");
  }

  private void validateCoden(Resource resource) {
    var resourceEdges = getEdges(getLiteInstance(resource), IDENTIFIER, ID_CODEN);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.get(0), MAP,
      List.of(IDENTIFIER, ID_CODEN),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("CODEN identifier name")
      ), "CODEN identifier name");
  }

  private void validateIsbn(Resource resource, String... expectedIsbnNames) {
    var resourceEdges = getEdges(getLiteInstance(resource), IDENTIFIER, ID_ISBN);
    assertThat(resourceEdges).hasSize(expectedIsbnNames.length);
    var index = 0;
    for (String expectedIsbnName : expectedIsbnNames) {
      validateEdge(resourceEdges.get(index), MAP,
        List.of(IDENTIFIER, ID_ISBN),
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of(expectedIsbnName)
        ), expectedIsbnName);
      index++;
    }
  }

  private void validateUnknown(Resource resource) {
    var resourceEdges = getEdges(getLiteInstance(resource), IDENTIFIER, ID_UNKNOWN);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.get(0), MAP,
      List.of(IDENTIFIER, ID_UNKNOWN),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("unknown identifier name"),
        "http://bibfra.me/vocab/lite/label", List.of("unknown identifier name")
      ), "unknown identifier name");
  }

  private void validateLccn(Resource resource) {
    var resourceEdges = getEdges(getLiteInstance(resource), IDENTIFIER, ID_LCCN);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.get(0), MAP,
      List.of(IDENTIFIER, ID_LCCN),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("LCCN identifier name"),
        "http://bibfra.me/vocab/lite/label", List.of("(DLC)LCCN identifier name")
      ), "(DLC)LCCN identifier name");
  }

  private void validateLiteInstanceTitle(Resource resource) {
    var resourceEdges = getEdges(getLiteInstance(resource), ResourceTypeDictionary.TITLE);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.get(0), TITLE,
      List.of(ResourceTypeDictionary.TITLE),
      Map.of(
        "http://bibfra.me/vocab/marc/mainTitle", List.of("work/instance title main title")
      ), "work/instance title main title");
  }
}
