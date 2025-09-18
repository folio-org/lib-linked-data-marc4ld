package org.folio.marc4ld.mapper.field008.government.publication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.GOVERNMENT_PUBLICATION;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getWorkEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.ResourceEdgeHelper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MarcToLdGovernmentPublicationIT extends Marc2LdTestBase {

  @ParameterizedTest
  @MethodSource("fileNames")
  void shouldMapGovernmentPublication_whenRecordIsBookOrSerial(String fileName) {
    // given
    var marc = loadResourceAsString(fileName);

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(this::getGovernmentPublicationEdge)
      .satisfies(e -> validateEdge(e, GOVERNMENT_PUBLICATION, List.of(CATEGORY),
        Map.of(
          "http://bibfra.me/vocab/library/code", List.of("a"),
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mgovtpubtype/a"),
          "http://bibfra.me/vocab/library/term", List.of("Autonomous")
        ),
        "Autonomous"))
      .extracting(ResourceEdgeHelper::getCategorySetEdge)
      .satisfies(e -> validateEdge(e, IS_DEFINED_BY, List.of(CATEGORY_SET),
        Map.of(
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mgovtpubtype"),
          "http://bibfra.me/vocab/lite/label", List.of("Government Publication Type")
        ),
        "Government Publication Type"))
      .extracting(ResourceEdgeHelper::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  private static Stream<Arguments> fileNames() {
    return Stream.of(
      Arguments.of("fields/008/marc_008_mgovtpubtype.jsonl"),
      Arguments.of("fields/008/marc_008_mgovtpubtype_serial.jsonl")
    );
  }

  private ResourceEdge getGovernmentPublicationEdge(Resource result) {
    return Optional.of(getWorkEdge(result))
      .map(this::getGovernmentPublicationEdge)
      .orElseThrow();
  }

  private ResourceEdge getGovernmentPublicationEdge(ResourceEdge result) {
    return getFirstOutgoingEdge(result, withPredicateUri("http://bibfra.me/vocab/library/governmentPublication"));
  }
}
