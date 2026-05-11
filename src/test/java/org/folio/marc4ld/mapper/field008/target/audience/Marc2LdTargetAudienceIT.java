package org.folio.marc4ld.mapper.field008.target.audience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.PredicateDictionary.TARGET_AUDIENCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONTINUING_RESOURCES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getOutgoingEdges;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.ResourceEdgeHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class Marc2LdTargetAudienceIT extends Marc2LdTestBase {

  @ParameterizedTest
  @CsvSource({
    "a, pre, Preschool",
    "b, pri, Primary",
    "c, pad, Pre-adolescent",
    "d, ado, Adolescent",
    "e, adu, Adult",
    "f, spe, Specialized",
    "g, gen, General",
    "j, juv, Juvenile"
  })
  void shouldMapTargetAudience_whenRecordIsMonograph(String code, String linkSuffix, String term) {
    // given
    var marc = """
        {
          "leader" : "00078nam a2200037uc 4500",
          "fields" : [ {
            "008" : "                      %s                "
          } ]
        }""".formatted(code);

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(ResourceEdgeHelper::getWorkEdge)
      .satisfies(we -> validateResource(we.getTarget(), List.of(WORK, BOOKS), Map.of(), ""))
      .extracting(workEdge -> getOutgoingEdges(workEdge, withPredicateUri("http://bibfra.me/vocab/library/targetAudience")))
      .satisfies(edges -> {
        assertThat(edges).hasSize(1);
        validateEdge(edges.getFirst(), TARGET_AUDIENCE, List.of(CATEGORY),
          Map.of(
            "http://bibfra.me/vocab/library/code", List.of(code),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/maudience/" + linkSuffix),
            "http://bibfra.me/vocab/library/term", List.of(term)
          ), term);
      })
      .extracting(edges -> getOutgoingEdges(edges.getFirst()))
      .satisfies(edges -> {
        assertThat(edges).hasSize(1);
        validateEdge(edges.getFirst(), IS_DEFINED_BY, List.of(CATEGORY_SET),
          Map.of(
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/maudience"),
            "http://bibfra.me/vocab/lite/label", List.of("Target audience")
          ), "Target audience");
        assertThat(getOutgoingEdges(edges.getFirst())).isEmpty();
      });
  }

  @Test
  void shouldNotMapTargetAudience_whenCodeIsInvalid() {
    // given — 'x' is not a valid target audience code
    var marc = """
        {
          "leader" : "00078nam a2200037uc 4500",
          "fields" : [ {
            "008" : "                      x                "
          } ]
        }""";

    //when
    var result = marcBibToResource(marc);

    //then
    var targetAudienceEdges = getOutgoingEdges(result, withPredicateUri("http://bibfra.me/vocab/lite/instantiates"))
      .stream()
      .flatMap(workEdge -> getOutgoingEdges(workEdge.getTarget(),
        withPredicateUri("http://bibfra.me/vocab/library/targetAudience")).stream())
      .toList();
    assertThat(targetAudienceEdges).isEmpty();
  }

  @Test
  void shouldNotMapTargetAudience_whenRecordIsSerial() {
    // given
    var marc = loadResourceAsString("fields/008/marc_008_target_audience_serial.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(ResourceEdgeHelper::getWorkEdge)
      .satisfies(we -> validateResource(we.getTarget(), List.of(WORK, CONTINUING_RESOURCES), Map.of(), ""))
      .extracting(ResourceEdge::getTarget)
      .extracting(work -> getOutgoingEdges(work, withPredicateUri("http://bibfra.me/vocab/library/targetAudience")))
      .satisfies(edges -> assertThat(edges).isEmpty());
  }
}
