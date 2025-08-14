package org.folio.marc4ld.service.marc2ld.bib.mapper.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class AdminMetadataCreatedDateMapperTest {
  @Mock
  private LabelService labelService;

  @Mock
  private MapperHelper mapperHelper;

  @Mock
  private FingerprintHashService hashService;

  @InjectMocks
  private AdminMetadataCreatedDateMapper mapper;

  @ParameterizedTest
  @CsvSource({
    "'      ', false",
    "'      990909', false",
    "990909, true",
    "' 1234 ', false",
    "abcdef, false",
    "'01101 ', false",
    "12, false",
    "121011          b, true",
  })
  void isMarcDate(String tagData, boolean isDate) {
    // when
    var result = mapper.isMarcDate(tagData);

    // then
    assertEquals(isDate, result);
  }

  @ParameterizedTest
  @CsvSource({
    "000101, 2000-01-01",
    "480229, 2048-02-29",
    "491231, 2049-12-31",
    "501101, 1950-11-01",
    "991231, 1999-12-31",
  })
  void formatMarcDateAsIsoDate(String inputDate, String formattedDate) {
    // when
    var marcDate = mapper.formatMarcDateAsIsoDate(inputDate);

    // then
    assertEquals(formattedDate, marcDate);
  }
}
