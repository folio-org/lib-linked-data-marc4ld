package org.folio.marc4ld.mapper.field008.frequency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.PUBLICATION_FREQUENCY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FREQUENCY;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getOutgoingEdges;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class MarcToLdPublicationFrequencyIT extends Marc2LdTestBase {

  @Test
  void shouldMapPublicationFrequency_whenRecordIsSerial() {
    // given
    var marc = """
      {
        "leader" : "00078n s a2200037uc 4500",
        "fields" : [ {
          "008" : "                  an                   "
        } ]
      }
      """;

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(i -> getOutgoingEdges(i, withPredicateUri("http://bibfra.me/vocab/marc/publicationFrequency")))
      .satisfies(edges -> validateEdge(edges.getFirst(), PUBLICATION_FREQUENCY, List.of(FREQUENCY),
        Map.of(
          "http://bibfra.me/vocab/marc/code", List.of("a"),
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/frequencies/ann"),
          "http://bibfra.me/vocab/lite/label", List.of("annual")
        ), "annual"))
      .satisfies(edges -> validateEdge(edges.getLast(), PUBLICATION_FREQUENCY, List.of(FREQUENCY),
        Map.of(
          "http://bibfra.me/vocab/marc/code", List.of("n"),
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/frequencies/irr"),
          "http://bibfra.me/vocab/lite/label", List.of("irregular")
        ), "irregular"));
  }

  @Test
  void shouldNotMapPublicationFrequency_whenRecordIsMonograph() {
    // given
    var marc = """
      {
        "leader" : "00078nam a2200037uc 4500",
        "fields" : [ {
          "008" : "                  an                   "
        } ]
      }
      """;

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(i -> getOutgoingEdges(i, withPredicateUri("http://bibfra.me/vocab/marc/publicationFrequency")))
      .satisfies(edges -> assertThat(edges).isEmpty());
  }

  @Test
  void shouldNotMapPublicationFrequency_whenNoDeterminableValue() {
    // given
    var marc = """
      {
        "leader" : "00078nam a2200037uc 4500",
        "fields" : [ {
          "008" : "                  #                    "
        } ]
      }
      """;

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(i -> getOutgoingEdges(i, withPredicateUri("http://bibfra.me/vocab/marc/publicationFrequency")))
      .satisfies(edges -> assertThat(edges).isEmpty());
  }

  @Test
  void shouldGracefullyHandleEmptyPublicationFrequency() {
    // given
    var emptyPublicationFrequencyMarc = """
      {
        "leader" : "      as                ",
        "fields" : [ {
          "008" : "                                   eng"
        } ]
      }""";

    //when
    var result = marcBibToResource(emptyPublicationFrequencyMarc);

    //then
    assertThat(result)
      .extracting(i -> getOutgoingEdges(i, withPredicateUri("http://bibfra.me/vocab/marc/publicationFrequency")))
      .satisfies(edges -> assertThat(edges).isEmpty());
  }

}
