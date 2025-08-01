package org.folio.marc4ld.service.marc2ld.bib.mapper.custom;

import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.SUPPLEMENTARY_CONTENT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.marc4j.marc.MarcFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
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

  @ParameterizedTest
  @CsvSource(value = {
    "b, bibliography",
    "k, discography",
    "q, filmography",
    "1, index"
  })
  void getLinkSuffix_shouldReturn_correctSuffix(char code, String expectedSuffix) {
    //expect
    assertEquals(expectedSuffix, mapper.getLinkSuffix(code));
  }

  @ParameterizedTest
  @CsvSource(value = {
    "b, bibliography",
    "k, discography",
    "q, filmography",
    "1, index"
  })
  void getTerm_shouldReturn_correctTerm(char code, String expectedTerm) {
    //expect
    assertEquals(expectedTerm, mapper.getTerm(code));
  }

  @ParameterizedTest
  @CsvSource(value = {
    "b, bibliography",
    "k, discography",
    "q, filmography",
    "1, index"
  })
  void getCode_shouldReturn_correctCode(char code, String expectedCode) {
    //expect
    assertEquals(expectedCode, mapper.getCode(code));
  }

  @Test
  void shouldNotMap() {
    //given
    var marcFactory = MarcFactory.newInstance();
    var marcRecord = marcFactory.newRecord();
    marcRecord.addVariableField(marcFactory.newControlField("008", SPACE.repeat(39)));
    var instance = createInstanceWithWork();

    //when
    mapper.map(marcRecord, instance);

    //then
    verifyNoInteractions(mapperHelper);
    verifyNoInteractions(labelService);
    verifyNoInteractions(hashService);
  }

  @Test
  void shouldMap() {
    //given
    var marcFactory = MarcFactory.newInstance();
    var marcRecord = marcFactory.newRecord();
    marcRecord.addVariableField(marcFactory.newControlField("008", "                        b      1       "));
    var instance = createInstanceWithWork();
    when(hashService.hash(any())).thenReturn(new Random().nextLong());
    when(hashService.hash(any())).thenReturn(new Random().nextLong());

    //when
    mapper.map(marcRecord, instance);

    //then
    verify(mapperHelper, new Times(4)).getJsonNode(any());
    verify(labelService, new Times(4)).setLabel(any(), anyMap());
    verify(hashService, new Times(4)).hash(any());
  }

  private Resource createInstanceWithWork() {
    var work = createResource(
      emptyMap(),
      Set.of(WORK, BOOKS),
      new EnumMap<>(PredicateDictionary.class)
    );

    return createResource(
      emptyMap(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }
}
