package org.folio.marc4ld.mapper.field020;

import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_ISBN;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateCancelledStatus;
import static org.folio.marc4ld.mapper.test.TestUtil.validateCurrentStatus;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getOutgoingEdges;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Marc2Ld020IT extends Marc2LdTestBase {

  @ParameterizedTest
  @MethodSource("isbnMappingArguments")
  void shouldMapField020(String marcFile, Map<String, List<String>> expectedProperties, String expectedLabel,
                         boolean isCancelled) {
    // given
    var marc = loadResourceAsString(marcFile);

    // when
    var result = marcBibToResource(marc);

    // then
    var isbnEdges = getOutgoingEdges(result, withPredicateUri(MAP.getUri()));
    assertEquals(1, isbnEdges.size());

    validateResource(isbnEdges.getFirst().getTarget(), List.of(IDENTIFIER, ID_ISBN), expectedProperties, expectedLabel);

    var statusEdge = getStatusEdge(isbnEdges.getFirst().getTarget());
    if (isCancelled) {
      validateCancelledStatus(statusEdge.getTarget());
    } else {
      validateCurrentStatus(statusEdge.getTarget());
    }
  }

  private static Stream<Arguments> isbnMappingArguments() {
    return Stream.of(
      Arguments.of(
        "fields/020/marc_020_a_with_qualifier.jsonl",
        Map.of(
          "http://bibfra.me/vocab/library/qualifier", List.of("hardcover"),
          "http://bibfra.me/vocab/lite/name", List.of("1000")
        ),
        "1000",
        false
      ),
      Arguments.of(
        "fields/020/marc_020_a_only.jsonl",
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of("1000")
        ),
        "1000",
        false
      ),
      Arguments.of(
        "fields/020/marc_020_z_with_qualifier.jsonl",
        Map.of(
          "http://bibfra.me/vocab/library/qualifier", List.of("black leather"),
          "http://bibfra.me/vocab/lite/name", List.of("1002")
        ),
        "1002",
        true
      ),
      Arguments.of(
        "fields/020/marc_020_z_only.jsonl",
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of("1002")
        ),
        "1002",
        true
      )
    );
  }

  private ResourceEdge getStatusEdge(Resource resource) {
    return getFirstOutgoingEdge(resource, withPredicateUri(STATUS.getUri()));
  }
}
