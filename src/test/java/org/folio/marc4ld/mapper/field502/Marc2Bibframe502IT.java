package org.folio.marc4ld.mapper.field502;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Bibframe502IT extends Marc2LdTestBase {

  @Test
  void shouldMapField502() {
    // given
    var marc = loadResourceAsString("fields/502/marc_502.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    assertThat(result)
      .extracting(this::getDissertationEdge)
      .satisfies(e -> validateEdge(e, PredicateDictionary.DISSERTATION,
        List.of(ResourceTypeDictionary.DISSERTATION),
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("dissertation label"),
          "http://bibfra.me/vocab/marc/degree", List.of("dissertation degree"),
          "http://bibfra.me/vocab/marc/dissertationYear", List.of("dissertation year"),
          "http://bibfra.me/vocab/marc/dissertationNote", List.of("dissertation note 1", "dissertation note 2"),
          "http://bibfra.me/vocab/marc/dissertationID", List.of("dissertation ID 1", "dissertation ID 2")
        ),
        "dissertation label"))
      .extracting(this::getGrantingInstitutionEdge)
      .satisfies(e -> validateEdge(e, PredicateDictionary.GRANTING_INSTITUTION,
        List.of(ResourceTypeDictionary.ORGANIZATION),
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of("dissertation granting institution")
        ),
        "dissertation granting institution"))
      .extracting(this::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  private ResourceEdge getDissertationEdge(Resource result) {
    return Optional.of(getWorkEdge(result))
      .map(this::getDissertationEdge)
      .orElseThrow();
  }

  private ResourceEdge getDissertationEdge(ResourceEdge edge) {
    return getFirstOutgoingEdge(edge, withPredicateUri("http://bibfra.me/vocab/scholar/dissertation"));
  }

  private ResourceEdge getGrantingInstitutionEdge(ResourceEdge edge) {
    return getFirstOutgoingEdge(edge, withPredicateUri("http://bibfra.me/vocab/marc/grantingInstitution"));
  }
}
