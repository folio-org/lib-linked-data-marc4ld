package org.folio.marc4ld.mapper.field041;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getWorkEdge;

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
    "a, http://bibfra.me/vocab/lite/language",
    "b, http://bibfra.me/vocab/lite/summaryLanguage",
    "d, http://bibfra.me/vocab/lite/sungOrSpokenTextLanguage",
    "e, http://bibfra.me/vocab/lite/librettoLanguage",
    "f, http://bibfra.me/vocab/lite/tableOfContentsLanguage",
    "g, http://bibfra.me/vocab/lite/accompanyingMaterialLanguage",
    "h, http://bibfra.me/vocab/lite/originalLanguage",
    "i, http://bibfra.me/vocab/lite/intertitlesLanguage",
    "j, http://bibfra.me/vocab/lite/subtitlesOrCaptionsLanguage",
    "k, http://bibfra.me/vocab/lite/intermediateTranslationsLanguage",
    "m, http://bibfra.me/vocab/lite/originalAccompanyingMaterialsLanguage",
    "n, http://bibfra.me/vocab/lite/originalLibrettoLanguage",
    "p, http://bibfra.me/vocab/lite/captionsLanguage",
    "q, http://bibfra.me/vocab/lite/accessibleAudioLanguage",
    "r, http://bibfra.me/vocab/lite/accessibleVisualMaterialLanguage",
    "t, http://bibfra.me/vocab/lite/accompanyingTranscriptsLanguage"
  })
  void shouldMapField041_asLanguageCategoryResourceUnderDifferentPredicates(String subfield, String predicate) {
    // given
    var marc = loadResourceAsString("fields/041/marc_041.jsonl")
      .replace("XXX", subfield);

    // when
    var result = marcBibToResource(marc);

    // then
    var work = getWorkEdge(result).getTarget();
    assertThat(work.getOutgoingEdges())
      .hasSize(1);
    validateLanguage(work.getOutgoingEdges().iterator().next(), predicate, "rus");
  }

  @Test
  void shouldMapField041_asLanguageWithSameLanguageFrom008() {
    // given
    var marc = loadResourceAsString("fields/041/marc_041_language_with_008.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = getWorkEdge(result).getTarget();
    assertThat(work.getOutgoingEdges())
      .hasSize(1);
    validateLanguage(work.getOutgoingEdges().iterator().next(), "http://bibfra.me/vocab/lite/language", "rus");
  }

  @Test
  void shouldMapField041_asLanguageWithAnotherLanguageFrom008() {
    // given
    var marc = loadResourceAsString("fields/041/marc_041_language_with_another_008.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = getWorkEdge(result).getTarget();
    assertThat(work.getOutgoingEdges())
      .hasSize(2);
    var iterator = work.getOutgoingEdges().iterator();
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/language", "rus");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/language", "eng");
  }

  @Test
  void shouldMapField041_withAllSubfields() {
    // given
    var marc = loadResourceAsString("fields/041/marc_041_full.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = getWorkEdge(result).getTarget();
    assertThat(work.getOutgoingEdges())
      .hasSize(16);
    var iterator = work.getOutgoingEdges().iterator();
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/language", "aaa");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/summaryLanguage", "bbb");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/sungOrSpokenTextLanguage", "ddd");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/librettoLanguage", "eee");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/tableOfContentsLanguage", "fff");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/accompanyingMaterialLanguage", "ggg");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/originalLanguage", "hhh");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/intertitlesLanguage", "iii");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/subtitlesOrCaptionsLanguage", "jjj");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/intermediateTranslationsLanguage", "kkk");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/originalAccompanyingMaterialsLanguage", "mmm");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/originalLibrettoLanguage", "nnn");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/captionsLanguage", "ppp");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/accessibleAudioLanguage", "qqq");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/accessibleVisualMaterialLanguage", "rrr");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/accompanyingTranscriptsLanguage", "ttt");
  }

  @Test
  void shouldMapField041_withRepeatedSubfields() {
    // given
    var marc = loadResourceAsString("fields/041/marc_041_repeated.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = getWorkEdge(result).getTarget();
    assertThat(work.getOutgoingEdges())
      .hasSize(24);
    var iterator = work.getOutgoingEdges().iterator();
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/language", "aaa");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/language", "aaa2");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/summaryLanguage", "bbb");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/summaryLanguage", "bbb2");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/sungOrSpokenTextLanguage", "ddd");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/sungOrSpokenTextLanguage", "ddd2");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/librettoLanguage", "eee");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/librettoLanguage", "eee2");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/tableOfContentsLanguage", "fff");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/tableOfContentsLanguage", "fff2");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/accompanyingMaterialLanguage", "ggg");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/accompanyingMaterialLanguage", "ggg2");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/originalLanguage", "hhh");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/originalLanguage", "hhh2");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/intertitlesLanguage", "iii");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/intertitlesLanguage", "iii2");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/subtitlesOrCaptionsLanguage", "jjj");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/subtitlesOrCaptionsLanguage", "jjj2");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/intermediateTranslationsLanguage", "kkk");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/intermediateTranslationsLanguage", "kkk2");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/originalAccompanyingMaterialsLanguage", "mmm");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/originalAccompanyingMaterialsLanguage", "mmm2");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/originalLibrettoLanguage", "nnn");
    validateLanguage(iterator.next(), "http://bibfra.me/vocab/lite/originalLibrettoLanguage", "nnn2");
  }

  private void validateLanguage(ResourceEdge resourceEdge, String predicate, String code) {
    validateEdge(
      resourceEdge,
      PredicateDictionary.fromUri(predicate).get(),
      List.of(ResourceTypeDictionary.LANGUAGE_CATEGORY),
      Map.of(
        CODE.getValue(), List.of(code),
        LINK.getValue(), List.of("http://id.loc.gov/vocabulary/languages/" + code)
      ),
      code
    );
  }
}
