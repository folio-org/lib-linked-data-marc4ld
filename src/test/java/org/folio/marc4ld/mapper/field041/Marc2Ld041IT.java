package org.folio.marc4ld.mapper.field041;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ACCESSIBLE_AUDIO_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.ACCESSIBLE_VISUAL_MATERIAL_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.ACCOMPANYING_MATERIAL_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.ACCOMPANYING_TRANSCRIPTS_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.CAPTIONS_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.INTERMEDIATE_TRANSLATIONS_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.INTERTITLES_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.LIBRETTO_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.ORIGINAL_ACCOMPANYING_MATERIALS_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.ORIGINAL_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.ORIGINAL_LIBRETTO_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.SUBTITLES_OR_CAPTIONS_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.SUMMARY_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.SUNG_OR_SPOKEN_TEXT_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.TABLE_OF_CONTENTS_LANGUAGE;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class Marc2Ld041IT extends Marc2LdTestBase {

  @ParameterizedTest
  @CsvSource(value = {
    "a, LANGUAGE",
    "b, SUMMARY_LANGUAGE",
    "d, SUNG_OR_SPOKEN_TEXT_LANGUAGE",
    "e, LIBRETTO_LANGUAGE",
    "f, TABLE_OF_CONTENTS_LANGUAGE",
    "g, ACCOMPANYING_MATERIAL_LANGUAGE",
    "h, ORIGINAL_LANGUAGE",
    "i, INTERTITLES_LANGUAGE",
    "j, SUBTITLES_OR_CAPTIONS_LANGUAGE",
    "k, INTERMEDIATE_TRANSLATIONS_LANGUAGE",
    "m, ORIGINAL_ACCOMPANYING_MATERIALS_LANGUAGE",
    "n, ORIGINAL_LIBRETTO_LANGUAGE",
    "p, CAPTIONS_LANGUAGE",
    "q, ACCESSIBLE_AUDIO_LANGUAGE",
    "r, ACCESSIBLE_VISUAL_MATERIAL_LANGUAGE",
    "t, ACCOMPANYING_TRANSCRIPTS_LANGUAGE"
  })
  void shouldMapField041_asLanguageCategoryResourceUnderDifferentPredicates(String subfield, String predicate) {
    // given
    var marc = loadResourceAsString("fields/041/marc_041.jsonl")
      .replace("XXX", subfield);

    // when
    var result = marcBibToResource(marc);

    // then
    var work = result.getOutgoingEdges().iterator().next().getTarget();
    assertThat(work.getOutgoingEdges())
      .hasSize(1);
    validateLanguage(work.getOutgoingEdges().iterator().next(), PredicateDictionary.valueOf(predicate), "rus");
  }

  @Test
  void shouldMapField041_asLanguageWithSameLanguageFrom008() {
    // given
    var marc = loadResourceAsString("fields/041/marc_041_language_with_008.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = result.getOutgoingEdges().iterator().next().getTarget();
    assertThat(work.getOutgoingEdges())
      .hasSize(1);
    validateLanguage(work.getOutgoingEdges().iterator().next(), LANGUAGE, "rus");
  }

  @Test
  void shouldMapField041_asLanguageWithAnotherLanguageFrom008() {
    // given
    var marc = loadResourceAsString("fields/041/marc_041_language_with_another_008.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = result.getOutgoingEdges().iterator().next().getTarget();
    assertThat(work.getOutgoingEdges())
      .hasSize(2);
    var iterator = work.getOutgoingEdges().iterator();
    validateLanguage(iterator.next(), LANGUAGE, "rus");
    validateLanguage(iterator.next(), LANGUAGE, "eng");
  }

  @Test
  void shouldMapField041_withAllSubfields() {
    // given
    var marc = loadResourceAsString("fields/041/marc_041_full.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = result.getOutgoingEdges().iterator().next().getTarget();
    assertThat(work.getOutgoingEdges())
      .hasSize(16);
    var iterator = work.getOutgoingEdges().iterator();
    validateLanguage(iterator.next(), LANGUAGE, "aaa");
    validateLanguage(iterator.next(), SUMMARY_LANGUAGE, "bbb");
    validateLanguage(iterator.next(), SUNG_OR_SPOKEN_TEXT_LANGUAGE, "ddd");
    validateLanguage(iterator.next(), LIBRETTO_LANGUAGE, "eee");
    validateLanguage(iterator.next(), TABLE_OF_CONTENTS_LANGUAGE, "fff");
    validateLanguage(iterator.next(), ACCOMPANYING_MATERIAL_LANGUAGE, "ggg");
    validateLanguage(iterator.next(), ORIGINAL_LANGUAGE, "hhh");
    validateLanguage(iterator.next(), INTERTITLES_LANGUAGE, "iii");
    validateLanguage(iterator.next(), SUBTITLES_OR_CAPTIONS_LANGUAGE, "jjj");
    validateLanguage(iterator.next(), INTERMEDIATE_TRANSLATIONS_LANGUAGE, "kkk");
    validateLanguage(iterator.next(), ORIGINAL_ACCOMPANYING_MATERIALS_LANGUAGE, "mmm");
    validateLanguage(iterator.next(), ORIGINAL_LIBRETTO_LANGUAGE, "nnn");
    validateLanguage(iterator.next(), CAPTIONS_LANGUAGE, "ppp");
    validateLanguage(iterator.next(), ACCESSIBLE_AUDIO_LANGUAGE, "qqq");
    validateLanguage(iterator.next(), ACCESSIBLE_VISUAL_MATERIAL_LANGUAGE, "rrr");
    validateLanguage(iterator.next(), ACCOMPANYING_TRANSCRIPTS_LANGUAGE, "ttt");
  }

  @Test
  void shouldMapField041_withRepeatedSubfields() {
    // given
    var marc = loadResourceAsString("fields/041/marc_041_repeated.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = result.getOutgoingEdges().iterator().next().getTarget();
    assertThat(work.getOutgoingEdges())
      .hasSize(32);
    var iterator = work.getOutgoingEdges().iterator();
    validateLanguage(iterator.next(), LANGUAGE, "aaa");
    validateLanguage(iterator.next(), LANGUAGE, "aaa2");
    validateLanguage(iterator.next(), SUMMARY_LANGUAGE, "bbb");
    validateLanguage(iterator.next(), SUMMARY_LANGUAGE, "bbb2");
    validateLanguage(iterator.next(), SUNG_OR_SPOKEN_TEXT_LANGUAGE, "ddd");
    validateLanguage(iterator.next(), SUNG_OR_SPOKEN_TEXT_LANGUAGE, "ddd2");
    validateLanguage(iterator.next(), LIBRETTO_LANGUAGE, "eee");
    validateLanguage(iterator.next(), LIBRETTO_LANGUAGE, "eee2");
    validateLanguage(iterator.next(), TABLE_OF_CONTENTS_LANGUAGE, "fff");
    validateLanguage(iterator.next(), TABLE_OF_CONTENTS_LANGUAGE, "fff2");
    validateLanguage(iterator.next(), ACCOMPANYING_MATERIAL_LANGUAGE, "ggg");
    validateLanguage(iterator.next(), ACCOMPANYING_MATERIAL_LANGUAGE, "ggg2");
    validateLanguage(iterator.next(), ORIGINAL_LANGUAGE, "hhh");
    validateLanguage(iterator.next(), ORIGINAL_LANGUAGE, "hhh2");
    validateLanguage(iterator.next(), INTERTITLES_LANGUAGE, "iii");
    validateLanguage(iterator.next(), INTERTITLES_LANGUAGE, "iii2");
    validateLanguage(iterator.next(), SUBTITLES_OR_CAPTIONS_LANGUAGE, "jjj");
    validateLanguage(iterator.next(), SUBTITLES_OR_CAPTIONS_LANGUAGE, "jjj2");
    validateLanguage(iterator.next(), INTERMEDIATE_TRANSLATIONS_LANGUAGE, "kkk");
    validateLanguage(iterator.next(), INTERMEDIATE_TRANSLATIONS_LANGUAGE, "kkk2");
    validateLanguage(iterator.next(), ORIGINAL_ACCOMPANYING_MATERIALS_LANGUAGE, "mmm");
    validateLanguage(iterator.next(), ORIGINAL_ACCOMPANYING_MATERIALS_LANGUAGE, "mmm2");
    validateLanguage(iterator.next(), ORIGINAL_LIBRETTO_LANGUAGE, "nnn");
    validateLanguage(iterator.next(), ORIGINAL_LIBRETTO_LANGUAGE, "nnn2");
    validateLanguage(iterator.next(), CAPTIONS_LANGUAGE, "ppp");
    validateLanguage(iterator.next(), CAPTIONS_LANGUAGE, "ppp2");
    validateLanguage(iterator.next(), ACCESSIBLE_AUDIO_LANGUAGE, "qqq");
    validateLanguage(iterator.next(), ACCESSIBLE_AUDIO_LANGUAGE, "qqq2");
    validateLanguage(iterator.next(), ACCESSIBLE_VISUAL_MATERIAL_LANGUAGE, "rrr");
    validateLanguage(iterator.next(), ACCESSIBLE_VISUAL_MATERIAL_LANGUAGE, "rrr2");
    validateLanguage(iterator.next(), ACCOMPANYING_TRANSCRIPTS_LANGUAGE, "ttt");
    validateLanguage(iterator.next(), ACCOMPANYING_TRANSCRIPTS_LANGUAGE, "ttt2");
  }

  private void validateLanguage(ResourceEdge resourceEdge, PredicateDictionary predicate, String code) {
    validateEdge(
      resourceEdge,
      predicate,
      List.of(ResourceTypeDictionary.LANGUAGE_CATEGORY),
      Map.of(
        CODE.getValue(), List.of(code),
        LINK.getValue(), List.of("http://id.loc.gov/vocabulary/languages/" + code)
      ),
      code
    );
  }
}
