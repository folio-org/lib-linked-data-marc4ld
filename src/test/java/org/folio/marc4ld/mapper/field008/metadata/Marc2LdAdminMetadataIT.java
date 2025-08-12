package org.folio.marc4ld.mapper.field008.metadata;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ADMIN_METADATA;
import static org.folio.ld.dictionary.PropertyDictionary.CREATED_DATE;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2LdAdminMetadataIT extends Marc2LdTestBase {

  @Test
  void shouldMapAdminMetadataOutOf001() {
    // given
    var marc = loadResourceAsString("fields/008/marc_008_admin_metadata.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    assertThat(result.getOutgoingEdges()).hasSize(1);
    var edge = result.getOutgoingEdges().iterator().next();
    assertThat(edge.getPredicate()).isEqualTo(ADMIN_METADATA);
    assertThat(edge.getTarget().getLabel()).isNotNull();
    assertThat(edge.getTarget().getDoc().has(CREATED_DATE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(CREATED_DATE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(CREATED_DATE.getValue()).get(0).asText()).isEqualTo("2024-11-20");
  }
}
