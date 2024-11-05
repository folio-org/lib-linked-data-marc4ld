package org.folio.marc4ld.mapper.field008;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.LANGUAGE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.LANGUAGE_CATEGORY;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.ResourceEdgeHelper;
import org.junit.jupiter.api.Test;

class MarcToLdLanguageIT extends Marc2LdTestBase {

  @Test
  void shouldMapLanguage() {
    // given
    var marc = loadResourceAsString("fields/008/marc_008_language.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(ResourceEdgeHelper::getWorkEdge)
      .extracting(this::getLanguageEdge)
      .satisfies(e -> validateEdge(e, LANGUAGE, List.of(LANGUAGE_CATEGORY),
        Map.of(
          "http://bibfra.me/vocab/marc/code", List.of("eng"),
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/languages/eng")
        ),
        "eng"))
      .extracting(ResourceEdgeHelper::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  private ResourceEdge getLanguageEdge(ResourceEdge result) {
    return getFirstOutgoingEdge(result, withPredicateUri("http://bibfra.me/vocab/lite/language"));
  }
}
