package org.folio.marc4ld.service.ld2marc.field.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class ControlFieldRuleApplierImplTest {

  private static final String TAG = "000";

  @Mock
  DictionaryProcessor dictionaryProcessor;

  @Test
  void map_withNull_shouldReturnEmptyList() {
    //given
    var fieldRule = createControlFieldRule(Collections.emptyMap());

    //when
    var parameters = fieldRule.map(null);

    //then
    assertThat(parameters)
      .isNotNull()
      .isEmpty();
  }

  @Test
  void map_withNull_shouldNotThrowAnyException() {
    //given
    var fieldRule = createControlFieldRule(Collections.emptyMap());

    //expected
    assertThatCode(() -> fieldRule.map(null))
      .doesNotThrowAnyException();
  }

  private ControlFieldRuleApplierImpl createControlFieldRule(Map<String, List<Integer>> rules) {
    return new ControlFieldRuleApplierImpl(
      TAG, rules, dictionaryProcessor
    );
  }
}
