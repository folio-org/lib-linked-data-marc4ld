package org.folio.marc4ld.mapper.field340;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.BOOK_FORMAT;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.ResourceEdgeHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MarcToLd340IT extends Marc2LdTestBase {

  @ParameterizedTest
  @MethodSource("provideStandardBookFormatParams")
  void shouldProcessStandardBookFormat(String fileName, String code, String term) {
    // given
    var marc = loadResourceAsString(fileName);

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(this::getBookFormatEdge)
      .satisfies(e -> validateEdge(e, BOOK_FORMAT, List.of(CATEGORY),
        Map.of(
          "http://bibfra.me/vocab/marc/code", List.of(code),
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/bookformat/" + code),
          "http://bibfra.me/vocab/marc/term", List.of(term),
          "http://bibfra.me/vocab/marc/source", List.of("rdabf")
        ),
        term))
      .extracting(this::getCategorySetEdge)
      .satisfies(e -> validateEdge(e, IS_DEFINED_BY, List.of(CATEGORY_SET),
        Map.of(
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/bookformat"),
          "http://bibfra.me/vocab/lite/label", List.of("Book Format")
        ),
        "Book Format"))
      .extracting(ResourceEdgeHelper::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  @Test
  void shouldProcessNonStandardBookFormat() {
    // given
    var marc = loadResourceAsString("fields/340/marc_340_non_standard_book_format.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .satisfies(resource -> validateResource(resource,
        List.of(INSTANCE),
        Map.of("http://bibfra.me/vocab/marc/bookFormat", List.of("book format label.")),
        ""))
      .satisfies(r -> assertThat(r.getOutgoingEdges()).isEmpty());
  }

  private ResourceEdge getBookFormatEdge(Resource result) {
    return getFirstOutgoingEdge(result, withPredicateUri("http://bibfra.me/vocab/marc/bookFormat"));
  }

  private ResourceEdge getCategorySetEdge(ResourceEdge edge) {
    return getFirstOutgoingEdge(edge, withPredicateUri("http://bibfra.me/vocab/lite/isDefinedBy"));
  }

  private static Stream<Arguments> provideStandardBookFormatParams() {
    return Stream.of(
      Arguments.of("fields/340/marc_340_standard_book_format.jsonl", "4to", "4to"),
      Arguments.of("fields/340/marc_340_full-sheet_book_format.jsonl", "full", "full-sheet")
    );
  }
}
