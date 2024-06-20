package org.folio.marc4ld.service.marc2ld.preprocessor.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor.PreprocessorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.impl.DataFieldImpl;
import org.marc4j.marc.impl.SubfieldImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Bib043PreprocessorTest {

  @Mock
  private MarcFactory factory;
  @Mock
  private DictionaryProcessor dictionaryProcessor;
  @InjectMocks
  private Bib043Preprocessor preprocessor;

  @Test
  void getTagsShouldReturnCorrectTags() {
    //expect
    assertEquals(List.of("043"), preprocessor.getTags());
  }

  @Test
  void preprocessShouldPreprocessSubfieldA() {
    //given
    var dataField = createDataField(List.of(createSubfield('a', "n-us---"), createSubfield('k', "a---")));
    var expectedDataField = createDataField(List.of(createSubfield('a', "n-us"), createSubfield('k', "a---")));
    when(factory.newDataField("043", ' ', ' ')).thenReturn(createDataField(List.of()));
    when(factory.newSubfield('a', "n-us")).thenReturn(createSubfield('a', "n-us"));
    when(dictionaryProcessor.getValue("GEOGRAPHIC_CODE_TO_NAME", "n-us")).thenReturn(Optional.of("United States"));

    //when
    var preprocessed = preprocessor.preprocess(new PreprocessorContext(mock(Record.class), dataField));

    //then
    assertThat(preprocessed)
      .usingRecursiveComparison()
      .isEqualTo(Optional.of(expectedDataField));
  }

  @Test
  void isValidShouldReturnFalse_whenSubfieldA_isNotPresent() {
    //given
    var dataField = createDataField(List.of(createSubfield('k', "data")));

    //expect
    assertFalse(preprocessor.isValid(dataField));
  }

  private static Stream<Arguments> provideArguments() {
    return Stream.of(
      Arguments.of(
        "isValid should return true when subfield 'a' is present and it's value is present in the NAME dictionary",
        Optional.of("value"),
        true
      ),
      Arguments.of(
        "isValid should return false when subfield 'a' is present and it's value isn't present in the NAME dictionary",
        Optional.empty(),
        false
      )
    );
  }

  @ParameterizedTest(name = "{index} {0}")
  @MethodSource("provideArguments")
  void isValid(String testName, Optional optional, boolean expectedResult) {
    //given
    var dataField = createDataField(List.of(createSubfield('a', "data")));
    when(dictionaryProcessor.getValue("GEOGRAPHIC_CODE_TO_NAME", "data")).thenReturn(optional);

    //when
    var result = preprocessor.isValid(dataField);

    //then
    assertEquals(expectedResult, result);
  }

  private Subfield createSubfield(char subfieldCode, String subfieldData) {
    return new SubfieldImpl(subfieldCode, subfieldData);
  }

  private DataField createDataField(List<Subfield> subfields) {
    var dataField = new DataFieldImpl("043", ' ', ' ');
    subfields.forEach(dataField::addSubfield);
    return dataField;
  }
}
