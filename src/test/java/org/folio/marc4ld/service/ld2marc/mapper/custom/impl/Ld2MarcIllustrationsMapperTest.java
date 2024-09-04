package org.folio.marc4ld.service.ld2marc.mapper.custom.impl;

import static org.folio.ld.dictionary.PredicateDictionary.ILLUSTRATIONS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Ld2MarcIllustrationsMapperTest {

  Ld2MarcIllustrationsMapper mapper = new Ld2MarcIllustrationsMapper();

  @Test
  void getPredicate_shouldReturn_correctPredicate() {
    //expect
    assertEquals(ILLUSTRATIONS, mapper.getPredicate());
  }

  @Test
  void getStartIndex_shouldReturn_18() {
    //expect
    assertEquals(18, mapper.getStartIndex());
  }

  @Test
  void getEndIndex_shouldReturn_22() {
    //expect
    assertEquals(22, mapper.getEndIndex());
  }
}
