package org.folio.marc4ld.service.marc2ld.normalization;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.Collection;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.marc2ld.reader.MarcReaderProcessor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Marc2LdPunctuationNormalizationIT {

  private static final String ABBREVIATION_PATTERN1 = "abbreviation A.";
  private static final String ABBREVIATION_PATTERN2 = "abbreviation A.B.";

  @Autowired
  private MarcReaderProcessor marcReaderProcessor;

  @Autowired
  private MarcBibPunctuationNormalizerImpl marcPunctuationNormalizer;

  @ParameterizedTest
  @ValueSource(strings = {
    "fields/normalization/normalization_full_marc_bib.jsonl",
    "fields/normalization/normalization_full_marc_authority.jsonl"}
  )
  void map_shouldNormalizeMarcRecord(String marc) {
    // given
    var initialMarc = loadResourceAsString(marc);

    // when
    var marcRecord = marcReaderProcessor.readMarc(initialMarc).findFirst();
    marcRecord.ifPresent(marcPunctuationNormalizer::normalize);

    // then
    assertThat(marcRecord)
      .get()
      .satisfies(this::isNormalized);
  }

  private void isNormalized(Record marcRecord) {
    marcRecord.getDataFields()
      .stream()
      .map(DataField::getSubfields)
      .flatMap(Collection::stream)
      .forEach(this::isNormalized);
  }

  private void isNormalized(Subfield subfield) {
    assertThat(subfield.getData())
      .isIn(
        ABBREVIATION_PATTERN1,
        ABBREVIATION_PATTERN2,
        String.valueOf(subfield.getCode())
      );
  }
}
