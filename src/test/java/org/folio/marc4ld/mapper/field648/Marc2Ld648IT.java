package org.folio.marc4ld.mapper.field648;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FORM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TEMPORAL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TOPIC;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getOutgoingEdges;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getWorkEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld648IT extends Marc2LdTestBase {

  @Test
  void shouldMapField648() {
    // given
    var marc = loadResourceAsString("fields/648/marc_648.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = getWorkEdge(result).getTarget();
    var subjectEdge = getFirstOutgoingEdge(work, withPredicateUri(SUBJECT.getUri()));
    var expectedConceptLabel = "1793 -- form 1 -- topic 1 -- topic 2 -- tmp 1 -- tmp 2 -- place 1 -- place 2";
    validateResource(
      subjectEdge.getTarget(),
      List.of(CONCEPT, TEMPORAL),
      Map.ofEntries(
        entry("http://bibfra.me/vocab/lite/name", List.of("1793")),
        entry("http://bibfra.me/vocab/library/formSubdivision", List.of("form 1")),
        entry("http://bibfra.me/vocab/library/generalSubdivision", List.of("topic 1", "topic 2")),
        entry("http://bibfra.me/vocab/library/chronologicalSubdivision", List.of("tmp 1", "tmp 2")),
        entry("http://bibfra.me/vocab/library/geographicSubdivision", List.of("place 1", "place 2")),
        entry("http://bibfra.me/vocab/library/relator_code", List.of("relator code")),
        entry("http://bibfra.me/vocab/lite/label", List.of(expectedConceptLabel)),
        entry("http://bibfra.me/vocab/library/fieldLink", List.of("field link")),
        entry("http://bibfra.me/vocab/library/source", List.of("source")),
        entry("http://bibfra.me/vocab/library/controlField", List.of("control field")),
        entry("http://bibfra.me/vocab/lite/equivalent", List.of("equivalent")),
        entry("http://bibfra.me/vocab/lite/authorityLink", List.of("https://id.loc.gov/authorities/names/n1234567890")),
        entry("http://bibfra.me/vocab/library/relator_term", List.of("setting.")),
        entry("http://bibfra.me/vocab/library/materialsSpecified", List.of("materials specified")),
        entry("http://bibfra.me/vocab/library/linkage", List.of("linkage"))
      ),
      expectedConceptLabel
    );

    var focusEdge = getFirstOutgoingEdge(subjectEdge, withPredicateUri(FOCUS.getUri()));
    validateResource(
      focusEdge.getTarget(),
      List.of(TEMPORAL),
      Map.ofEntries(
        Map.entry("http://bibfra.me/vocab/lite/label", List.of("1793")),
        Map.entry("http://bibfra.me/vocab/library/fieldLink", List.of("field link")),
        Map.entry("http://bibfra.me/vocab/library/source", List.of("source")),
        Map.entry("http://bibfra.me/vocab/library/controlField", List.of("control field")),
        Map.entry("http://bibfra.me/vocab/lite/equivalent", List.of("equivalent")),
        Map.entry("http://bibfra.me/vocab/lite/authorityLink", List.of("https://id.loc.gov/authorities/names/n1234567890")),
        Map.entry("http://bibfra.me/vocab/lite/name", List.of("1793")),
        Map.entry("http://bibfra.me/vocab/library/materialsSpecified", List.of("materials specified")),
        Map.entry("http://bibfra.me/vocab/library/linkage", List.of("linkage"))
      ),
      "1793"
    );

    // Validate subFocus resources
    var subFocusEdges = getOutgoingEdges(subjectEdge, withPredicateUri(SUB_FOCUS.getUri()));
    assertThat(subFocusEdges).hasSize(7);
    var expectedSubFocuses = Map.of(
      "form 1", new TypeAndProperties(FORM,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("form 1"),
          "http://bibfra.me/vocab/lite/name", List.of("form 1"))
      ),
      "topic 1", new TypeAndProperties(TOPIC,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("topic 1"),
          "http://bibfra.me/vocab/lite/name", List.of("topic 1"))
      ),
      "topic 2", new TypeAndProperties(TOPIC,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("topic 2"),
          "http://bibfra.me/vocab/lite/name", List.of("topic 2"))
      ),
      "tmp 1", new TypeAndProperties(TEMPORAL,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("tmp 1"),
          "http://bibfra.me/vocab/lite/name", List.of("tmp 1"))
      ),
      "tmp 2", new TypeAndProperties(TEMPORAL,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("tmp 2"),
          "http://bibfra.me/vocab/lite/name", List.of("tmp 2"))
      ),
      "place 1", new TypeAndProperties(PLACE,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("place 1"),
          "http://bibfra.me/vocab/lite/name", List.of("place 1"))
      ),
      "place 2", new TypeAndProperties(PLACE,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("place 2"),
          "http://bibfra.me/vocab/lite/name", List.of("place 2"))
      )
    );

    subFocusEdges
      .stream()
      .map(ResourceEdge::getTarget)
      .forEach(r -> {
        var label = r.getLabel();
        var properties = expectedSubFocuses.get(label);
        validateResource(r, List.of(properties.type()), properties.properties(), label);
      });
  }
}
