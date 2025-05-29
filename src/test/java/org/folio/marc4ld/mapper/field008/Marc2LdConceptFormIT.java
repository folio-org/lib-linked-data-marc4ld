package org.folio.marc4ld.mapper.field008;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.GENRE;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FORM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getOutgoingEdges;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.ThrowingConsumer;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.ResourceEdgeHelper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

class Marc2LdConceptFormIT extends Marc2LdTestBase {

  @Test
  void shouldMapConceptForm() {
    // given
    var marc = loadResourceAsString("fields/008/marc_008_concept_form.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .extracting(ResourceEdgeHelper::getWorkEdge)
      .extracting(workEdge -> getOutgoingEdges(workEdge, withPredicateUri("http://bibfra.me/vocab/lite/subject")))
      .satisfies(conceptFormEdges -> {
        assertThat(conceptFormEdges).hasSize(4);
        validateEdge(conceptFormEdges.getFirst(), SUBJECT, List.of(CONCEPT, FORM),
          Map.of(
            "http://bibfra.me/vocab/lite/name", List.of("Catalogs")
          ), "Catalogs");
        validateEdge(conceptFormEdges.get(1), SUBJECT, List.of(CONCEPT, FORM),
          Map.of(
            "http://bibfra.me/vocab/lite/name", List.of("Indexes")
          ), "Indexes");
        validateEdge(conceptFormEdges.get(2), SUBJECT, List.of(CONCEPT, FORM),
          Map.of(
            "http://bibfra.me/vocab/lite/name", List.of("Calendars")
          ), "Calendars");
        validateEdge(conceptFormEdges.get(3), SUBJECT, List.of(CONCEPT, FORM),
          Map.of(
            "http://bibfra.me/vocab/lite/name", List.of("Comics (Graphic works)")
          ), "Comics (Graphic works)");
      })
      .extracting(conceptFormEdges ->
        conceptFormEdges.stream()
          .map(ResourceEdge::getTarget)
          .map(target -> getOutgoingEdges(target, withPredicateUri("http://bibfra.me/vocab/lite/focus")))
          .flatMap(List::stream)
          .toList()
      )
      .satisfies(validateFocusEdges(FOCUS))
      .extracting(focusEdges ->
        focusEdges.stream()
          .map(ResourceEdge::getTarget)
          .map(target -> getOutgoingEdges(target, withPredicateUri("http://library.link/vocab/map")))
          .flatMap(List::stream)
          .toList()
      )
      .satisfies(lccnEdges -> {
        assertThat(lccnEdges).hasSize(4);
        validateEdge(lccnEdges.getFirst(), MAP, List.of(ID_LCCN, IDENTIFIER),
          Map.of(
            "http://bibfra.me/vocab/lite/name", List.of("gf2014026057"),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/authorities/genreForms/gf2014026057"),
            "http://bibfra.me/vocab/lite/label", List.of("gf2014026057")
          ), "gf2014026057");
        validateEdge(lccnEdges.get(1), MAP, List.of(ID_LCCN, IDENTIFIER),
          Map.of(
            "http://bibfra.me/vocab/lite/name", List.of("gf2014026112"),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/authorities/genreForms/gf2014026112"),
            "http://bibfra.me/vocab/lite/label", List.of("gf2014026112")
          ), "gf2014026112");
        validateEdge(lccnEdges.get(2), MAP, List.of(ID_LCCN, IDENTIFIER),
          Map.of(
            "http://bibfra.me/vocab/lite/name", List.of("gf2014026055"),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/authorities/genreForms/gf2014026055"),
            "http://bibfra.me/vocab/lite/label", List.of("gf2014026055")
          ), "gf2014026055");
        validateEdge(lccnEdges.get(3), MAP, List.of(ID_LCCN, IDENTIFIER),
          Map.of(
            "http://bibfra.me/vocab/lite/name", List.of("gf2014026266"),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/authorities/genreForms/gf2014026266"),
            "http://bibfra.me/vocab/lite/label", List.of("gf2014026266")
          ), "gf2014026266");
      })
      .extracting(lccnEdges ->
        lccnEdges.stream()
          .map(ResourceEdge::getTarget)
          .map(Resource::getOutgoingEdges)
          .flatMap(Set::stream)
          .toList()
      )
      .satisfies(emptyEdges -> assertThat(emptyEdges).isEmpty());

    assertThat(result)
      .extracting(ResourceEdgeHelper::getWorkEdge)
      .extracting(workEdge -> getOutgoingEdges(workEdge, withPredicateUri("http://bibfra.me/vocab/lite/genre")))
      .satisfies(validateFocusEdges(GENRE));
  }

  private static @NotNull ThrowingConsumer<List<ResourceEdge>> validateFocusEdges(PredicateDictionary predicate) {
    return focusEdges -> {
      assertThat(focusEdges).hasSize(4);
      validateEdge(focusEdges.getFirst(), predicate, List.of(FORM),
        Map.of("http://bibfra.me/vocab/lite/name", List.of("Catalogs")), "Catalogs");
      validateEdge(focusEdges.get(1), predicate, List.of(FORM),
        Map.of("http://bibfra.me/vocab/lite/name", List.of("Indexes")), "Indexes");
      validateEdge(focusEdges.get(2), predicate, List.of(FORM),
        Map.of("http://bibfra.me/vocab/lite/name", List.of("Calendars")), "Calendars");
      validateEdge(focusEdges.get(3), predicate, List.of(FORM),
        Map.of("http://bibfra.me/vocab/lite/name", List.of("Comics (Graphic works)")), "Comics (Graphic works)");
    };
  }
}
