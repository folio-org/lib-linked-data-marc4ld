package org.folio.marc4ld.service.marc2ld.preprocessor.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.TestUtil.createDataField;
import static org.folio.marc4ld.TestUtil.createSubfield;
import static org.folio.marc4ld.TestUtil.validateSubfield;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor.PreprocessorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.marc4j.marc.MarcFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrailingPunctuationPreprocessorTest {

  @Mock
  private MarcFactory factory;
  @InjectMocks
  private TrailingPunctuationPreprocessor preprocessor;

  @Test
  void getTagsShouldReturnCorrectTags() {
    //expect
    assertEquals(List.of("260", "262", "264", "300"), preprocessor.getTags());
  }

  @ParameterizedTest
  @CsvSource(value = {
    "New York : , New York",
    "'HarperCollins,' , HarperCollins",
    "2020. , 2020",
    "50 pages ; , 50 pages",
    "'24 cm. ' , 24 cm",
    "[Explicit edition] , [Explicit edition]",
    "'text; ! ,.  ;:  ' , text; !",
  })
  void preprocess_shouldRemove_trailingPunctuation(String originalValue, String expectedValue) {
    //given
    var subfields = List.of(
      createSubfield('a', originalValue)
    );
    var dataField = createDataField("300", subfields);
    var context = new PreprocessorContext(null, dataField);
    when(factory.newDataField("300", ' ', ' ')).thenReturn(createDataField("300", new ArrayList<>()));

    //when
    var result = preprocessor.preprocess(context);

    //then
    assertThat(result)
      .isPresent()
      .get()
      .satisfies(df -> assertEquals("300", df.getTag()))
      .satisfies(df -> assertEquals(' ', df.getIndicator1()))
      .satisfies(df -> assertEquals(' ', df.getIndicator2()))
      .satisfies(df -> validateSubfield(df, 'a', expectedValue));
  }
}
