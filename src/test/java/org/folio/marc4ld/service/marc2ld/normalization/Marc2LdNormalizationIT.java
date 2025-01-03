package org.folio.marc4ld.service.marc2ld.normalization;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.marc2ld.reader.MarcReaderProcessor;
import org.junit.jupiter.api.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Marc2LdNormalizationIT {

  @Autowired
  private MarcReaderProcessor marcReaderProcessor;

  @Autowired
  private MarcPunctuationNormalizer marcPunctuationNormalizer;

  @Test
  void map_shouldNormalizeMarcRecord() {
    // given
    var initialMarc = loadResourceAsString("fields/normalization/normalization_full_marc.jsonl");

    // when
    var marcRecord = marcReaderProcessor.readMarc(initialMarc).findFirst();
    marcRecord.ifPresent(marcPunctuationNormalizer::normalize);

    // then
    assertThat(marcRecord)
      .get()
      .satisfies(this::notContainsPunctuationsAfterNormalization);
  }

  private void notContainsPunctuationsAfterNormalization(Record marcRecord) {
    marcRecord.getDataFields()
      .stream()
      .map(DataField::getSubfields)
      .flatMap(Collection::stream)
      .forEach(subfield -> assertEquals(String.valueOf(subfield.getCode()), subfield.getData()));
  }
}
