package org.folio.marc4ld.service.marc2ld.normalization;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapper;
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
  private Ld2MarcMapper ld2MarcMapper;

  @Autowired
  private MarcReaderProcessor marcReaderProcessor;

  @Autowired
  private MarcRecordNormalizer marcRecordNormalizer;

  @Test
  void map_shouldNormalizeMarcRecord() {
    // given
    var initialMarc = loadResourceAsString("fields/normalization/normalization_full_marc.jsonl");

    // when
    var recordStream = marcReaderProcessor.readMarc(initialMarc);

    // then
    assertThat(recordStream.findFirst())
      .get()
      .satisfies(this::notContainsPunctuationsAfterNormalization);
  }

  private void notContainsPunctuationsAfterNormalization(Record marcRecord) {
    marcRecordNormalizer.normalize(marcRecord);
    marcRecord.getDataFields()
      .stream()
      .map(DataField::getSubfields)
      .flatMap(Collection::stream)
      .forEach(subfield -> assertEquals(String.valueOf(subfield.getCode()), subfield.getData()));
  }
}
