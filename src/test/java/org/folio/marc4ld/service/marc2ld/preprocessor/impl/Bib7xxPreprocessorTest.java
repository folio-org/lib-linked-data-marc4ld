package org.folio.marc4ld.service.marc2ld.preprocessor.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.TestUtil.createDataField;
import static org.folio.marc4ld.TestUtil.createSubfield;
import static org.folio.marc4ld.TestUtil.validateSubfield;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor.PreprocessorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.impl.DataFieldImpl;
import org.marc4j.marc.impl.SubfieldImpl;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Bib7xxPreprocessorTest {

  @Test
  void getTagsShouldReturnCorrectTags() {
    //given
    var preprocessor = new Bib7xxPreprocessor(mock(MarcFactory.class));

    //expect
    assertEquals(List.of("776"), preprocessor.getTags());
  }

  private static Stream<Arguments> provideArguments() {
    return Stream.of(
      Arguments.of(
        mock(Record.class),
        getMarcFactory(null),
        List.of(
          createSubfield('a', "aValue"),
          createSubfield('s', "sValue"),
          createSubfield('t', "tValue")
        ),
        "sValue", "tValue"
      ),
      Arguments.of(
        mock(Record.class),
        getMarcFactory("tValue", 's'),
        List.of(
          createSubfield('a', "aValue"),
          createSubfield('t', "tValue")
        ),
        "tValue", "tValue"
      ),
      Arguments.of(
        getMarcRecord(
          createSubfield('a', "MainTitle"),
          createSubfield('b', "SubTitle")
        ),
        getMarcFactory("MainTitle SubTitle", 't'),
        List.of(
          createSubfield('a', "aValue"),
          createSubfield('s', "sValue")
        ),
        "sValue", "MainTitle SubTitle"
      ),
      Arguments.of(
        getMarcRecord(
          createSubfield('a', "MainTitle")
        ),
        getMarcFactory("MainTitle", 's', 't'),
        List.of(
          createSubfield('a', "aValue")
        ),
        "MainTitle", "MainTitle"
      )
    );
  }

  @ParameterizedTest
  @MethodSource("provideArguments")
  void preprocess_shouldPreprocessSubfields_relatedToTitle(Record marcRecord, MarcFactory factory,
                                                           List<Subfield> subfields,
                                                           String subfieldExpectedValueS,
                                                           String subfieldExpectedValueT) {
    //given
    var preprocessor = new Bib7xxPreprocessor(factory);
    var dataField = createDataField("776", subfields);

    //when
    var result = preprocessor.preprocess(new PreprocessorContext(marcRecord, dataField));

    //then
    assertThat(result)
      .isPresent()
      .get()
      .satisfies(df -> assertEquals("776", df.getTag()))
      .satisfies(df -> assertEquals(' ', df.getIndicator1()))
      .satisfies(df -> assertEquals(' ', df.getIndicator2()))
      .satisfies(df -> validateSubfield(df, 'a', "aValue"))
      .satisfies(df -> validateSubfield(df, 's', subfieldExpectedValueS))
      .satisfies(df -> validateSubfield(df, 't', subfieldExpectedValueT));
  }

  private static MarcFactory getMarcFactory(String subfieldValue, char... subfields) {
    var factory = mock(MarcFactory.class);
    when(factory.newDataField("776", ' ', ' ')).thenReturn(new DataFieldImpl("776", ' ', ' '));
    if (subfieldValue != null) {
      for (char subfield : subfields) {
        when(factory.newSubfield(subfield, subfieldValue)).thenReturn(new SubfieldImpl(subfield, subfieldValue));
      }
    }
    return factory;
  }

  private static Record getMarcRecord(Subfield... subfields) {
    var marcRecord = mock(Record.class);
    when(marcRecord.getDataFields())
      .thenReturn(List.of(createDataField("245", List.of(subfields))));
    return marcRecord;
  }
}
