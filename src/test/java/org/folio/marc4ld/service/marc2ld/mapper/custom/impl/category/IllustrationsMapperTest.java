package org.folio.marc4ld.service.marc2ld.mapper.custom.impl.category;

import static org.folio.ld.dictionary.PredicateDictionary.ILLUSTRATIONS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.mapper.MapperHelper;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class IllustrationsMapperTest {

  @Mock
  LabelService labelService;

  @Mock
  MapperHelper mapperHelper;

  @Mock
  FingerprintHashService hashService;

  @InjectMocks
  private IllustrationsMapper mapper;

  @ParameterizedTest
  @ValueSource(chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'o', 'p'})
  void isSupportedCode_shouldReturn_true(char code) {
    //expect
    assertTrue(mapper.isSupportedCode(code));
  }

  @ParameterizedTest
  @ValueSource(chars = {'q', '1', '@', 'A'})
  void isSupportedCode_shouldReturn_false(char code) {
    //expect
    assertFalse(mapper.isSupportedCode(code));
  }

  @Test
  void getPredicate_shouldReturn_illustrations() {
    //expect
    assertEquals(ILLUSTRATIONS, mapper.getPredicate());
  }

  @ParameterizedTest
  @CsvSource(value = {
    "a, ill",
    "b, map",
    "c, por",
    "d, chr",
    "e, pln",
    "f, plt",
    "g, mus",
    "h, mus",
    "i, coa",
    "j, gnt",
    "k, for",
    "l, sam",
    "m, pho",
    "o, pht",
    "p, ilm",
  })
  void getLinkSuffix_shouldReturn_correctSuffix(char code, String expectedSuffix) {
    //expect
    assertEquals(expectedSuffix, mapper.getLinkSuffix(code));
  }

  @ParameterizedTest
  @CsvSource(value = {
    "a, Illustrations",
    "b, Maps",
    "c, Portraits",
    "d, Charts",
    "e, Plans",
    "f, Plates",
    "g, Music",
    "h, Facsimiles",
    "i, Coats of arms",
    "j, Genealogical tables",
    "k, Forms",
    "l, Samples",
    "m, 'Phonodisc, phonowire, etc.'",
    "o, Photographs",
    "p, Illuminations",
  })
  void getTerm_shouldReturn_correctTerm(char code, String expectedTerm) {
    //expect
    assertEquals(expectedTerm, mapper.getTerm(code));
  }

  @ParameterizedTest
  @CsvSource(value = {
    "a, a",
    "b, b",
    "c, c",
    "d, d",
    "e, e",
    "f, f",
    "g, g",
    "h, h",
    "i, i",
    "j, j",
    "k, k",
    "l, l",
    "m, m",
    "o, o",
    "p, p",
  })
  void getCode_shouldReturn_correctCode(char code, String expectedCode) {
    //expect
    assertEquals(expectedCode, mapper.getCode(code));
  }
}
