package org.folio.marc4ld.service.marc2ld.mapper.custom.impl;

import static org.folio.ld.dictionary.PredicateDictionary.SUPPLEMENTARY_CONTENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.mapper.MapperHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplementaryContentMapperTest {

  @Mock
  LabelService labelService;

  @Mock
  MapperHelper mapperHelper;

  @Mock
  FingerprintHashService hashService;

  @InjectMocks
  private SupplementaryContentMapper mapper;

  @Test
  void getStartIndex_shouldReturn_18() {
    //expect
    assertEquals(24, mapper.getStartIndex());
  }

  @Test
  void getEndIndex_shouldReturn_22() {
    //expect
    assertEquals(28, mapper.getEndIndex());
  }

  @ParameterizedTest
  @ValueSource(chars = {'b', 'k', 'q'})
  void isSupportedCode_shouldReturn_true(char code) {
    //expect
    assertTrue(mapper.isSupportedCode(code));
  }

  @ParameterizedTest
  @ValueSource(chars = {'z', '1', '@', 'A'})
  void isSupportedCode_shouldReturn_false(char code) {
    //expect
    assertFalse(mapper.isSupportedCode(code));
  }

  @Test
  void getPredicate_shouldReturn_illustrations() {
    //expect
    assertEquals(SUPPLEMENTARY_CONTENT, mapper.getPredicate());
  }

  @Test
  void getCategorySetLink_shouldReturn_millus() {
    //expect
    assertEquals("http://id.loc.gov/vocabulary/msupplcont", mapper.getCategorySetLink());
  }

  @Test
  void getCategorySetLabel_shouldReturn_illustrativeContent() {
    //expect
    assertEquals("Supplementary Content", mapper.getCategorySetLabel());
  }

  @ParameterizedTest
  @CsvSource(value = {
    "b, bibliography",
    "k, discography",
    "q, film"
  })
  void getLinkSuffix_shouldReturn_correctSuffix(char code, String expectedSuffix) {
    //expect
    assertEquals(expectedSuffix, mapper.getLinkSuffix(code));
  }

  @ParameterizedTest
  @CsvSource(value = {
    "b, bibliography",
    "k, discography",
    "q, film"
  })
  void getTerm_shouldReturn_correctTerm(char code, String expectedTerm) {
    //expect
    assertEquals(expectedTerm, mapper.getTerm(code));
  }
}
