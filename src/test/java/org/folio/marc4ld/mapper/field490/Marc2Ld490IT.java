package org.folio.marc4ld.mapper.field490;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.IS_PART_OF;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_ISSN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.SERIES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdgeWithSource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstIncomingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getWorkEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld490IT extends Marc2LdTestBase {

  @Test
  void shouldMapField490() {
    // given
    var marc = loadResourceAsString("fields/490/marc_490.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    assertThat(getWorkEdge(result).getTarget())
      .extracting(work -> getFirstOutgoingEdge(work, withPredicateUri("http://bibfra.me/vocab/relation/isPartOf")))
      .satisfies(this::validateWorkSeries)
      .extracting(workSeries ->
        getFirstOutgoingEdge(workSeries, withPredicateUri("http://bibfra.me/vocab/relation/isPartOf")))
      .satisfies(this::validateSeries)
      .extracting(series -> getFirstIncomingEdge(series, withPredicateUri("http://bibfra.me/vocab/lite/instantiates")))
      .satisfies(this::validateInstanceSeries)
      .extracting(instanceSeries ->
        getFirstOutgoingEdge(instanceSeries.getSource(), withPredicateUri("http://library.link/vocab/map")))
      .satisfies(this::validateIssnIdentifier);
  }

  private void validateWorkSeries(ResourceEdge workSeries) {
    validateEdge(workSeries, IS_PART_OF, List.of(WORK, SERIES),
      Map.of(
        "http://bibfra.me/vocab/marc/volume", List.of("volume"),
        "http://bibfra.me/vocab/lite/label", List.of("name volume"),
        "http://bibfra.me/vocab/lite/name", List.of("name")), "name volume");
  }

  private void validateSeries(ResourceEdge series) {
    validateEdge(series, IS_PART_OF, List.of(SERIES),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("name"),
        "http://bibfra.me/vocab/lite/name", List.of("name")), "name");
  }

  private void validateInstanceSeries(ResourceEdge instanceSeries) {
    validateEdgeWithSource(instanceSeries, INSTANTIATES, List.of(INSTANCE, SERIES),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("name"),
        "http://bibfra.me/vocab/lite/name", List.of("name")), "name");
  }

  private void validateIssnIdentifier(ResourceEdge issnIdentifier) {
    validateEdge(issnIdentifier, MAP, List.of(ID_ISSN, IDENTIFIER),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("issn"),
        "http://bibfra.me/vocab/lite/name", List.of("issn")), "issn");
  }
}
