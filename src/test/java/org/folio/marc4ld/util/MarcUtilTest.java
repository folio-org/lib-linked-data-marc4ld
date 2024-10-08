package org.folio.marc4ld.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

class MarcUtilTest {

  @Test
  void isSubfieldPresent_shouldReturn_True_when_subfieldIsPresent() {
    //given
    var dataField = mock(DataField.class);
    var code = 'a';
    when(dataField.getSubfield(code)).thenReturn(mock(Subfield.class));

    //expect
    assertTrue(MarcUtil.isSubfieldPresent(code, dataField));
  }

  @Test
  void isSubfieldPresent_shouldReturn_False_when_subfieldIsNotPresent() {
    //given
    var dataField = mock(DataField.class);
    var code = 'a';
    when(dataField.getSubfield(code)).thenReturn(null);

    //expect
    assertFalse(MarcUtil.isSubfieldPresent(code, dataField));
  }

  @Test
  void getSubfieldValue_shouldReturnCorrespondingSubfieldValue() {
    //given
    var dataField = mock(DataField.class);
    var subfield = mock(Subfield.class);
    var code = 'a';
    var value = "value";
    when(dataField.getSubfield(code)).thenReturn(subfield);
    when(subfield.getData()).thenReturn(value);

    //expect
    assertEquals(value, MarcUtil.getSubfieldValue(code, dataField));
  }

  @Test
  void isLanguageMaterial_shouldReturn_True() {
    //expect
    assertTrue(MarcUtil.isLanguageMaterial('a'));
  }

  @ParameterizedTest
  @ValueSource(chars = {
    'b',
    ' ',
    '1',
    '!'
  })
  void isLanguageMaterial_shouldReturn_False(char c) {
    //expect
    assertFalse(MarcUtil.isLanguageMaterial(c));
  }

  @ParameterizedTest
  @ValueSource(chars = {
    'a',
    'm'
  })
  void isMonographicComponentPartOrItem_shouldReturn_True(char c) {
    //expect
    assertTrue(MarcUtil.isMonographicComponentPartOrItem(c));
  }

  @ParameterizedTest
  @ValueSource(chars = {
    'b',
    ' ',
    '1',
    '!'
  })
  void isMonographicComponentPartOrItem_shouldReturn_False(char c) {
    //expect
    assertFalse(MarcUtil.isLanguageMaterial(c));
  }
}
