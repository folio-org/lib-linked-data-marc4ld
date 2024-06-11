package org.folio.marc4ld.service.ld2marc.resource.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.MarcFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ControlFieldsBuilderTest {

  @Mock
  private MarcFactory factory;
  @Captor
  private ArgumentCaptor<String> tagCaptor;
  @Captor
  private ArgumentCaptor<String> dataCaptor;

  @Test
  void shouldUseTagsAndValue() {
    //given
    var tag = "test";
    var value = "hello";
    var startPosition = 5;
    var endPosition = startPosition + value.length();

    //when
    var actualElements = getFieldsByTestBuilder(tag, value, startPosition, endPosition);

    //then
    assertThat(actualElements)
      .size()
      .isEqualTo(2);

    assertThat(tagCaptor.getValue())
      .isEqualTo(tag);
    assertThat(dataCaptor.getValue())
      .contains(value);
  }

  @Test
  void shouldContainsStringLength() {
    //given
    var tag = "test";
    var value = "hello";
    var startPosition = 5;
    var endPosition = startPosition + value.length();

    //when
    getFieldsByTestBuilder(tag, value, startPosition, endPosition);

    //then
    assertThat(dataCaptor.getValue())
      .hasSize(39);
  }

  @ParameterizedTest
  @CsvSource(value = {
    "1999 , 7 ",
    "ilu  , 15",
    "b    , 22",
    "a    , 28",
    "eng  , 35"
  })
  void shouldFitStartPosition(String value, int startPosition) {
    //given
    var tag = "test";
    var endPosition = startPosition + value.length();

    //when
    getFieldsByTestBuilder(tag, value, startPosition, endPosition);

    //then
    var data = dataCaptor.getValue();
    assertThat(data.indexOf(value))
      .isEqualTo(startPosition);
  }

  @ParameterizedTest
  @CsvSource(value = {
    "1999 , 2 ",
    "1999 , 3 ",
    "1999 , 4 ",
    "1999 , 5 "
  })
  void shouldFitEndPosition(String value, int endPosition) {
    //given
    var tag = "test";
    var startPosition = 1;

    //when
    getFieldsByTestBuilder(tag, value, startPosition, endPosition);

    //then
    var data = dataCaptor.getValue();
    assertThat(data.charAt(endPosition))
      .isEqualTo(' ');
  }

  @ParameterizedTest
  @CsvSource(value = {
    "199987 , 7, 8  , 1     ",
    "199987 , 7, 9  , 19    ",
    "199987 , 7, 10 , 199   ",
    "199987 , 7, 11 , 1999  ",
    "199987 , 7, 12 , 19998 ",
  })
  void shouldCutValue(String givenValue, int startPosition, int endPosition, String expectedValue) {
    //given
    var tag = "test";

    //when
    getFieldsByTestBuilder(tag, givenValue, startPosition, endPosition);

    //then
    var data = dataCaptor.getValue();
    assertThat(data)
      .contains(expectedValue)
      .doesNotContain(givenValue);
  }

  private List<ControlField> getFieldsByTestBuilder(String tag, String value, int startPos, int endPos) {
    var builder = new ControlFieldsBuilder();
    builder.addFieldValue(tag, value, startPos, endPos);
    var transformFields = builder.build(factory)
      .toList();
    verify(factory, times(2))
      .newControlField(tagCaptor.capture(), dataCaptor.capture());
    return transformFields;
  }
}
