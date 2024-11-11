package org.folio.marc4ld.service.marc2ld.field.property.transformer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertyTransformerFactoryImplTest {

  @Mock
  private SeparatePropertyTransformer separatePropertyTransformer;
  @Mock
  private DefaultPropertyTransformer defaultPropertyTransformer;

  @InjectMocks
  private PropertyTransformerFactoryImpl propertyTransformerFactory;

  @Test
  void whenRuleIsMultiply_andSubfieldsIsOneThenReturnSeparateTransformer() {
    // given
    var rule = Mockito.mock(Marc4LdRules.FieldRule.class);
    when(rule.isMultiply())
      .thenReturn(true);
    when(rule.getSubfields())
      .thenReturn(Map.of('1', "one"));

    // when
    var result = propertyTransformerFactory.get(rule);

    // then
    assertThat(result)
      .isInstanceOf(SeparatePropertyTransformer.class);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 2, 3, 4, 5})
  void whenRuleIsMultiply_andSubfieldsIsOneThenReturnSeparateTransformer(int size) {
    // given
    var rule = Mockito.mock(Marc4LdRules.FieldRule.class);
    Map<Character, String> subfields = Mockito.mock(Map.class);
    when(subfields.size())
      .thenReturn(size);
    when(rule.isMultiply())
      .thenReturn(true);
    when(rule.getSubfields())
      .thenReturn(subfields);

    // expected
    assertThatExceptionOfType(IllegalArgumentException.class)
      .isThrownBy(() -> propertyTransformerFactory.get(rule))
      .withMessage("Only 1 subfield is required for multiply rule");
  }
}
