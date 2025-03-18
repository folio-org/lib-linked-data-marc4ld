package org.folio.marc4ld.service.marc2ld.preprocessor.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.TestUtil.createDataField;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.impl.DataFieldImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class Bib545PreprocessorTest {

  @Mock
  private MarcFactory factory;

  @InjectMocks
  private Bib545Preprocessor preprocessor;

  @Test
  void getTagsShouldReturnCorrectTags() {
    //expect
    assertEquals(List.of("545"), preprocessor.getTags());
  }

  @ParameterizedTest
  @CsvSource(value = {
    "0, 0",
    "1, 1",
    "/, ' '",
    "#, ' '",
    "%, ' '"
  })
  void preprocessShouldPreprocessIndicator1(char indicator1, char expectedIndicator1) {
    //given
    var tag = "545";
    var dataField = createDataField(tag, indicator1);
    when(factory.newDataField(tag, dataField.getIndicator1(), dataField.getIndicator2()))
      .thenReturn(new DataFieldImpl(tag, dataField.getIndicator1(), dataField.getIndicator2()));

    //when
    var result = preprocessor.preprocess(new DataFieldPreprocessor.PreprocessorContext(mock(Record.class), dataField));

    //then
    assertThat(result)
      .isPresent()
      .get()
      .satisfies(df -> assertEquals(tag, df.getTag()))
      .satisfies(df -> assertEquals(expectedIndicator1, df.getIndicator1()))
      .satisfies(df -> assertEquals(' ', df.getIndicator2()));
  }
}
