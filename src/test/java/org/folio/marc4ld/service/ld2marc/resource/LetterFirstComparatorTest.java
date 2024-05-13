package org.folio.marc4ld.service.ld2marc.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.marc4j.marc.Subfield;
import org.mockito.Mockito;

class LetterFirstComparatorTest {

  private final Comparator<Subfield> comparator = new LetterFirstComparator();

  @ParameterizedTest
  @CsvSource({
    "A , B, -1, 'A should come before B'          ",
    "B , A,  1, 'B should come after A'           ",
    "1 , 2, -1, '1 should come before 2'          ",
    "2 , 1,  1, '2 should come after 1'           ",
    "A , 1, -1, 'Letter should come before digit' ",
    "1 , A,  1, 'Digit should come after letter'  ",
    "B , B,  0, 'Same character should be equal'  ",
    "1 , 1,  0, 'Same digit should be equal'      "
  })
  void whenCompareFields(char sf1Code, char sf2Code, int expected, String message) {
    //given
    var sf1 = createSubfield(sf1Code);
    var sf2 = createSubfield(sf2Code);

    //when
    var result = comparator.compare(sf1, sf2);

    //then
    assertThat(result)
      .as(message)
      .isEqualTo(expected);
  }

  private Subfield createSubfield(char code) {
    var sf = Mockito.mock(Subfield.class);
    Mockito.when(sf.getCode())
      .thenReturn(code);
    return sf;
  }
}
