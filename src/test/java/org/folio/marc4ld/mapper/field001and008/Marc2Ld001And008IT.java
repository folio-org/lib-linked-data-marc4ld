package org.folio.marc4ld.mapper.field001and008;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ADMIN_METADATA;
import static org.folio.ld.dictionary.PropertyDictionary.CONTROL_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.CREATED_DATE;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld001And008IT extends Marc2LdTestBase {

  @Test
  void shouldMapAdminMetadataOutOf001And008() {
    // given
    var marc = loadResourceAsString("fields/001_008/marc_001_008.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    assertThat(result.getOutgoingEdges()).hasSize(1);
    var edge = result.getOutgoingEdges().iterator().next();
    assertThat(edge.getPredicate()).isEqualTo(ADMIN_METADATA);
    assertThat(edge.getTarget().getLabel()).isNotNull();
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    assertThat(edge.getTarget().getDoc().has(CREATED_DATE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(CREATED_DATE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(CREATED_DATE.getValue()).get(0).asText()).isEqualTo("112024");
    assertThat(edge.getTarget().getDoc().has(CONTROL_NUMBER.getValue()));
    assertThat(edge.getTarget().getDoc().get(CONTROL_NUMBER.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(CONTROL_NUMBER.getValue()).get(0).asText())
      .isEqualTo("#880524405##");
  }
}
