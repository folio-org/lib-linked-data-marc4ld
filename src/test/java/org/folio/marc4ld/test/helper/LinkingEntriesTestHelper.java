package org.folio.marc4ld.test.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.EXTENT;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.OTHER_EDITION;
import static org.folio.ld.dictionary.PredicateDictionary.OTHER_VERSION;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
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
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getWorkEdge;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.spring.testing.type.UnitTest;

@UnitTest
public class LinkingEntriesTestHelper {

  private static final Set<PredicateDictionary> LINKING_ENTRIES_PREDICATES = Set.of(OTHER_EDITION, OTHER_VERSION);

  public static void validateLiteWork(Resource resource, PredicateDictionary predicate, String expectedLabel) {
    var resourceEdges = getEdges(getWorkEdge(resource).getTarget(), WORK);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.getFirst(), predicate,
      List.of(WORK),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of(expectedLabel)
      ), expectedLabel);
  }

  public static List<ResourceEdge> getEdges(Resource resource, ResourceTypeDictionary... resourceTypes) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(edge -> Optional.of(edge.getTarget())
        .map(Resource::getTypes)
        .filter(types -> CollectionUtils.containsAll(types, Arrays.asList(resourceTypes)))
        .isPresent())
      .toList();
  }

  public static void validateAgent(Resource resource, String... expectedNames) {
    var resourceEdges = getEdges(getLiteWork(resource), AGENT);
    assertThat(resourceEdges).hasSize(expectedNames.length);
    var index = 0;
    for (var expectedName : expectedNames) {
      validateEdge(resourceEdges.get(index), CREATOR,
        List.of(AGENT),
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of(expectedName)
        ), expectedName);
      index++;
    }
  }

  public static Resource getLiteWork(Resource resource) {
    return getWorkEdge(resource)
      .getTarget()
      .getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> LINKING_ENTRIES_PREDICATES.contains(resourceEdge.getPredicate()))
      .map(ResourceEdge::getTarget)
      .findFirst()
      .orElseThrow();
  }

  public static void validateIssn(Resource resource) {
    var resourceEdges = getEdges(getLiteWork(resource), IDENTIFIER, ID_ISSN);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.getFirst(), MAP,
      List.of(IDENTIFIER, ID_ISSN),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("ISSN identifier name"),
        "http://bibfra.me/vocab/lite/label", List.of("ISSN identifier name")
      ), "ISSN identifier name");
  }

  public static void validateLiteWorkTitle(Resource resource, String expectedMainTitle) {
    var resourceEdges = getEdges(getLiteWork(resource), ResourceTypeDictionary.TITLE);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.getFirst(), TITLE,
      List.of(ResourceTypeDictionary.TITLE),
      Map.of(
        "http://bibfra.me/vocab/marc/mainTitle", List.of(expectedMainTitle),
        "http://bibfra.me/vocab/marc/qualifier", List.of("work title qualifier")
      ), expectedMainTitle);
  }

  public static void validateLiteInstance(Resource resource, int expectedEdgesSize, String expectedWorkLabel) {
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
    var resourceEdges = getEdges(liteInstance, WORK);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.getFirst(), INSTANTIATES,
      List.of(WORK),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of(expectedWorkLabel)
      ), expectedWorkLabel);
  }

  public static Resource getLiteInstance(Resource resource) {
    return getLiteWork(resource)
      .getIncomingEdges()
      .stream()
      .filter(resourceEdge -> INSTANTIATES == resourceEdge.getPredicate())
      .map(ResourceEdge::getSource)
      .findFirst()
      .orElseThrow();
  }

  public static void validateExtent(Resource resource) {
    var resourceEdges = getEdges(getLiteInstance(resource), ResourceTypeDictionary.EXTENT);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.getFirst(), EXTENT,
      List.of(ResourceTypeDictionary.EXTENT),
      Map.of(
        LABEL.getValue(), List.of("extent")
      ), "extent");
  }

  public static void validateLocalId(Resource resource, String... expectedLocalIds) {
    var resourceEdges = getEdges(getLiteInstance(resource), IDENTIFIER, ID_LOCAL);
    assertThat(resourceEdges).hasSize(expectedLocalIds.length);
    var index = 0;
    for (var expectedLocalId : expectedLocalIds) {
      validateEdge(resourceEdges.get(index), MAP,
        List.of(IDENTIFIER, ID_LOCAL),
        Map.of(
          "http://bibfra.me/vocab/marc/localId", List.of(expectedLocalId)
        ), expectedLocalId);
      index++;
    }
  }

  public static void validateStrn(Resource resource) {
    var resourceEdges = getEdges(getLiteInstance(resource), IDENTIFIER, ID_STRN);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.getFirst(), MAP,
      List.of(IDENTIFIER, ID_STRN),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("STRN identifier name")
      ), "STRN identifier name");
  }

  public static void validateCoden(Resource resource) {
    var resourceEdges = getEdges(getLiteInstance(resource), IDENTIFIER, ID_CODEN);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.getFirst(), MAP,
      List.of(IDENTIFIER, ID_CODEN),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("CODEN identifier name")
      ), "CODEN identifier name");
  }

  public static void validateIsbn(Resource resource, String... expectedIsbnNames) {
    var resourceEdges = getEdges(getLiteInstance(resource), IDENTIFIER, ID_ISBN);
    assertThat(resourceEdges).hasSize(expectedIsbnNames.length);
    var index = 0;
    for (var expectedIsbnName : expectedIsbnNames) {
      validateEdge(resourceEdges.get(index), MAP,
        List.of(IDENTIFIER, ID_ISBN),
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of(expectedIsbnName)
        ), expectedIsbnName);
      index++;
    }
  }

  public static void validateUnknown(Resource resource) {
    var resourceEdges = getEdges(getLiteInstance(resource), IDENTIFIER, ID_UNKNOWN);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.getFirst(), MAP,
      List.of(IDENTIFIER, ID_UNKNOWN),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("unknown identifier name"),
        "http://bibfra.me/vocab/lite/label", List.of("unknown identifier name")
      ), "unknown identifier name");
  }

  public static void validateLccn(Resource resource) {
    var resourceEdges = getEdges(getLiteInstance(resource), IDENTIFIER, ID_LCCN);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.getFirst(), MAP,
      List.of(IDENTIFIER, ID_LCCN),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("LCCN identifier name"),
        "http://bibfra.me/vocab/lite/label", List.of("(DLC)LCCN identifier name")
      ), "(DLC)LCCN identifier name");
  }

  public static void validateLiteInstanceTitle(Resource resource) {
    var resourceEdges = getEdges(getLiteInstance(resource), ResourceTypeDictionary.TITLE);
    assertThat(resourceEdges).hasSize(1);
    validateEdge(resourceEdges.getFirst(), TITLE,
      List.of(ResourceTypeDictionary.TITLE),
      Map.of(
        "http://bibfra.me/vocab/marc/mainTitle", List.of("work/instance title main title")
      ), "work/instance title main title");
  }
}
