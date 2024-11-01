package org.folio.marc4ld.mapper.field651;

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
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Bibframe651IT extends Marc2LdTestBase {

  @Test
  void shouldMapField651() {
    // given
    var marc = loadResourceAsString("fields/651/marc_651.jsonl");

    // when
    var resource = marcBibToResource(marc);

    //then
    var work = getWorkEdge(resource).getTarget();
    var subjectEdge = getFirstOutgoingEdge(work, withPredicateUri(SUBJECT.getUri()));

    // validate Concept resource
    var expectedConceptLabel = "name";
    validateResource(
      subjectEdge.getTarget(),
      List.of(PLACE, CONCEPT),
      Map.ofEntries(
        Map.entry("http://bibfra.me/vocab/marc/chronologicalSubdivision", List.of("temporal 1", "temporal 2")),
        Map.entry("http://bibfra.me/vocab/marc/relator_code", List.of("relator code")),
        Map.entry("http://bibfra.me/vocab/marc/fieldLink", List.of("field link")),
        Map.entry("http://bibfra.me/vocab/marc/source", List.of("source")),
        Map.entry("http://bibfra.me/vocab/marc/controlField", List.of("control field")),
        Map.entry("http://bibfra.me/vocab/lite/equivalent", List.of("equivalent")),
        Map.entry("http://bibfra.me/vocab/marc/miscInfo", List.of("misc info 1", "misc info 2")),
        Map.entry("http://bibfra.me/vocab/lite/authorityLink", List.of("authority link")),
        Map.entry("http://bibfra.me/vocab/marc/generalSubdivision", List.of("topic 1", "topic 2")),
        Map.entry("http://bibfra.me/vocab/marc/relator_term", List.of("relator term")),
        Map.entry("http://bibfra.me/vocab/lite/name", List.of("name")),
        Map.entry("http://bibfra.me/vocab/marc/materialsSpecified", List.of("materials specified")),
        Map.entry("http://bibfra.me/vocab/marc/linkage", List.of("linkage")),
        Map.entry("http://bibfra.me/vocab/marc/geographicSubdivision", List.of("place 1", "place 2")),
        Map.entry("http://bibfra.me/vocab/marc/formSubdivision", List.of("form 1", "form 2"))
      ),
      expectedConceptLabel);

    // Validate focus resource
    var focusEdge = getFirstOutgoingEdge(subjectEdge, withPredicateUri(FOCUS.getUri()));
    validateResource(
      focusEdge.getTarget(),
      List.of(PLACE),
      Map.of(
        "http://bibfra.me/vocab/marc/controlField", List.of("control field"),
        "http://bibfra.me/vocab/lite/equivalent", List.of("equivalent"),
        "http://bibfra.me/vocab/marc/miscInfo", List.of("misc info 1", "misc info 2"),
        "http://bibfra.me/vocab/lite/authorityLink", List.of("authority link"),
        "http://bibfra.me/vocab/lite/label", List.of("name"),
        "http://bibfra.me/vocab/lite/name", List.of("name"),
        "http://bibfra.me/vocab/marc/materialsSpecified", List.of("materials specified"),
        "http://bibfra.me/vocab/marc/linkage", List.of("linkage"),
        "http://bibfra.me/vocab/marc/fieldLink", List.of("field link"),
        "http://bibfra.me/vocab/marc/source", List.of("source")
      ),
      "name"
    );

    // Validate subFocus resources
    var subFocusEdges = getOutgoingEdges(subjectEdge, withPredicateUri(SUB_FOCUS.getUri()));
    assertThat(subFocusEdges).hasSize(8);
    var expectedSubFocuses = Map.of(
      "form 1", new TypeAndProperties(FORM,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("form 1"),
          "http://bibfra.me/vocab/lite/name", List.of("form 1"))
      ),
      "form 2", new TypeAndProperties(FORM,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("form 2"),
          "http://bibfra.me/vocab/lite/name", List.of("form 2"))
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
      "temporal 1", new TypeAndProperties(TEMPORAL,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("temporal 1"),
          "http://bibfra.me/vocab/lite/name", List.of("temporal 1"))
      ),
      "temporal 2", new TypeAndProperties(TEMPORAL,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("temporal 2"),
          "http://bibfra.me/vocab/lite/name", List.of("temporal 2"))
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
        validateResource(r, List.of(properties.type), properties.properties, label);
      });
  }

  record TypeAndProperties(ResourceTypeDictionary type, Map<String, List<String>> properties) {
  }
}
