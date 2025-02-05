package org.folio.marc4ld.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.TestUtil.extractTags;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.TAG_005;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.Constants.TAG_043;
import static org.folio.marc4ld.util.Constants.TAG_245;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.impl.ControlFieldImpl;
import org.marc4j.marc.impl.DataFieldImpl;
import org.marc4j.marc.impl.RecordImpl;

@UnitTest
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

  @Test
  void sortFields_shouldSortFieldsOfMarcRecord() {
    //given
    var marcRecord = new RecordImpl();
    marcRecord.addVariableField(new ControlFieldImpl(TAG_008));
    marcRecord.addVariableField(new ControlFieldImpl(TAG_005));
    marcRecord.addVariableField(new DataFieldImpl(TAG_245, SPACE, SPACE));
    marcRecord.addVariableField(new DataFieldImpl(TAG_043, SPACE, SPACE));

    //when
    MarcUtil.sortFields(marcRecord);

    //then
    assertThat(extractTags(marcRecord)).isEqualTo(List.of(TAG_005, TAG_008, TAG_043, TAG_245));
  }
}
