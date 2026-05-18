package org.folio.marc4ld.service.marc2ld.normalization;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.Collection;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.marc2ld.reader.MarcReaderProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
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

  @Autowired
  private MarcAuthorityPunctuationNormalizerImpl marcAuthorityPunctuationNormalizer;

  @Test
  void map_shouldNormalizeMarBibRecord() {
    // given
    var initialMarc = loadResourceAsString("fields/normalization/normalization_full_marc_bib.jsonl");

    // when
    var marcRecord = marcReaderProcessor.readMarc(initialMarc).findFirst();
    marcRecord.ifPresent(marcPunctuationNormalizer::normalize);

    // then
    assertThat(marcRecord)
      .get()
      .satisfies(this::isNormalized);
  }

  @Test
  void map_shouldNormalizeMarcAuthorityRecord() {
    // given
    var initialMarc = loadResourceAsString("fields/normalization/normalization_full_marc_authority.jsonl");

    // when
    var marcRecord = marcReaderProcessor.readMarc(initialMarc).findFirst();
    marcRecord.ifPresent(marcAuthorityPunctuationNormalizer::normalize);

    // then
    assertThat(marcRecord)
      .get()
      .satisfies(this::isNormalized);
  }

  @ParameterizedTest
  @CsvSource({
    "100, d, '(date)', date",
    "100, d, '[date]', date",
    "100, q, '(qualifier)', qualifier",
    "100, e, '(relator)', relator",
    "245, h, '(medium)', medium",
    "245, h, '[electronic resource]', electronic resource",
    "260, a, '(New York)', New York",
    "260, b, '[Publisher]', Publisher",
    "260, c, '()] 2022)', 2022",
    "264, a, '(Place)', Place",
    "264, b, '[Publisher]', Publisher",
    "264, c, '(2022)', 2022",
    "610, e, '(role)', role",
    "711, c, '(city)', city",
    "711, d, '(date)', date",
    "711, n, '(number)', number",
  })
  void shouldNormalizeBibSubfieldParenthesesAndSquareBrackets(String tag, char code, String input, String expected) {
    var factory = MarcFactory.newInstance();
    var record = factory.newRecord();
    var field = factory.newDataField(tag, ' ', ' ');
    field.addSubfield(factory.newSubfield(code, input));
    record.addVariableField(field);

    marcPunctuationNormalizer.normalize(record);

    assertThat(record.getDataFields().getFirst().getSubfield(code).getData())
      .isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({
    "100, a, name., name",
    "245, a, title., title",
    "246, a, title., title",
    "264, a, place., place",
    "336, a, 'text,', text",
    "336, a, text., text",
    "337, a, 'unmediated,', unmediated",
    "337, a, unmediated., unmediated",
    "338, a, 'volume,', volume",
    "338, a, volume., volume",
    "505, a, contents., contents",
    "520, a, abstract., abstract",
    "520, a, 'abstract ;', abstract",
  })
  void shouldNormalizeBibLastSubfieldTrailingPunctuation(String tag, char code, String input, String expected) {
    var factory = MarcFactory.newInstance();
    var record = factory.newRecord();
    var field = factory.newDataField(tag, ' ', ' ');
    field.addSubfield(factory.newSubfield(code, input));
    record.addVariableField(field);

    marcPunctuationNormalizer.normalize(record);

    assertThat(record.getDataFields().getFirst().getSubfield(code).getData())
      .isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({
    "100, a, 'author,', c, author",
    "245, a, 'title :', b, title",
    "245, a, 'title /', c, title",
    "246, a, 'title:', a, title",
    "264, a, 'place :', b, place",
    "300, a, 'extent :', b, extent",
    "300, b, 'illustration ;', c, illustration",
    "505, a, 'contents --', g, contents",
  })
  void shouldNormalizeBibSubfieldTrailingPunctuationWhenFollowedByNextSubfield(
      String tag, char subfield, String input, char nextSubfield, String expected) {
    var factory = MarcFactory.newInstance();
    var record = factory.newRecord();
    var field = factory.newDataField(tag, ' ', ' ');
    field.addSubfield(factory.newSubfield(subfield, input));
    field.addSubfield(factory.newSubfield(nextSubfield, "x"));
    record.addVariableField(field);

    marcPunctuationNormalizer.normalize(record);

    assertThat(record.getDataFields().getFirst().getSubfield(subfield).getData())
      .isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({
    "336, a, 'Rees, Paul A.,', 'Rees, Paul A.'",
    "336, a, 'Nancy W.K.,', 'Nancy W.K.'",
  })
  void shouldPreserveBibLastSubfieldAbbreviationPeriod(String tag, char code, String input, String expected) {
    var factory = MarcFactory.newInstance();
    var record = factory.newRecord();
    var field = factory.newDataField(tag, ' ', ' ');
    field.addSubfield(factory.newSubfield(code, input));
    record.addVariableField(field);

    marcPunctuationNormalizer.normalize(record);

    assertThat(record.getDataFields().getFirst().getSubfield(code).getData())
      .isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({
    "100, a, 'Rees, Paul A.,', 2, 'Rees, Paul A.'",
    "100, a, 'Nancy W.K.,', 2, 'Nancy W.K.'",
  })
  void shouldPreserveBibSubfieldAbbreviationPeriodWhenFollowedByNextSubfield(
      String tag, char subfield, String input, char nextSubfield, String expected) {
    var factory = MarcFactory.newInstance();
    var record = factory.newRecord();
    var field = factory.newDataField(tag, ' ', ' ');
    field.addSubfield(factory.newSubfield(subfield, input));
    field.addSubfield(factory.newSubfield(nextSubfield, "x"));
    record.addVariableField(field);

    marcPunctuationNormalizer.normalize(record);

    assertThat(record.getDataFields().getFirst().getSubfield(subfield).getData())
      .isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({
    "100, ' ', a, 'name,', c, name",
    "100, ' ', a, 'name ;', d, name",
    "100, ' ', a, 'name,', d, name",
    "100, ' ', a, 'name ;', v, name",
    "100, ' ', a, 'name,', v, name",
    "100, ' ', a, 'form.', k, form",
    "100, ' ', k, 'lang.', l, lang",
    "100, ' ', l, 'text,', m, text",
    "100, ' ', l, 'num.', n, num",
    "100, ' ', l, 'num,', n, num",
    "100, ' ', n, 'key,', r, key",
    "100, ' ', a, 'title.', t, title",
    "100, 3, a, 'family,', c, family",
    "100, 3, a, 'family ;', d, family",
    "100, 3, a, 'title.', t, title",
    "110, ' ', a, 'corp.', b, corp",
    "110, ' ', a, 'corp,', c, corp",
    "110, ' ', a, 'corp ;', d, corp",
    "111, ' ', a, 'conf :', c, conf",
    "111, ' ', c, 'place,', d, place",
    "111, ' ', d, 'date.', q, date",
  })
  void shouldNormalizeAuthoritySubfieldTrailingPunctuationWhenFollowedByNextSubfield(
      String tag, char ind1, char subfield, String input, char nextSubfield, String expected) {
    var factory = MarcFactory.newInstance();
    var record = factory.newRecord();
    var field = factory.newDataField(tag, ind1, ' ');
    field.addSubfield(factory.newSubfield(subfield, input));
    field.addSubfield(factory.newSubfield(nextSubfield, "x"));
    record.addVariableField(field);

    marcAuthorityPunctuationNormalizer.normalize(record);

    assertThat(record.getDataFields().getFirst().getSubfield(subfield).getData())
      .isEqualTo(expected);
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
