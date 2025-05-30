package org.folio.marc4ld.mapper.field001;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ADMIN_METADATA;
import static org.folio.ld.dictionary.PropertyDictionary.CONTROL_NUMBER;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld001IT extends Marc2LdTestBase {

  @Test
  void shouldMapAdminMetadataOutOf001() {
    // given
    var marc = loadResourceAsString("fields/001/marc_001.jsonl");
    var controlNumber = "#880524405##";

    // when
    var result = marcBibToResource(marc);

    // then
    assertThat(result.getOutgoingEdges()).hasSize(1);
    var edge = result.getOutgoingEdges().iterator().next();
    assertThat(edge.getPredicate()).isEqualTo(ADMIN_METADATA);
    assertThat(edge.getTarget().getLabel()).isEqualTo(controlNumber);
    assertThat(edge.getTarget().getDoc().has(CONTROL_NUMBER.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(CONTROL_NUMBER.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(CONTROL_NUMBER.getValue()).get(0).asText()).isEqualTo(controlNumber);
  }
}
