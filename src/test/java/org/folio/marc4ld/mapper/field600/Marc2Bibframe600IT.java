package org.folio.marc4ld.mapper.field600;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class Marc2Bibframe600IT extends Marc2LdTestBase {

  @ParameterizedTest
  @CsvSource(value = {
    "fields/600/marc_600_person.jsonl, http://bibfra.me/vocab/lite/Person",
    "fields/600/marc_600_family.jsonl, http://bibfra.me/vocab/lite/Family"
  })
  void shouldMapField600(String marcFile, String subjectType) {
    // given
    var marc = loadResourceAsString(marcFile);

    // when
    var resource = marcBibToResource(marc);

    //then
    var work = getWorkEdge(resource).getTarget();
    var subjectEdge = getFirstOutgoingEdge(work, withPredicateUri(SUBJECT.getUri()));

    // validate Concept resource
    var expectedConceptLabel = "numeration, name, title 1, title 2, name alternative, 2024 -- form 1 -- form 2 "
      + "-- topic 1 -- topic 2 -- temporal 1 -- temporal 2 -- place 1 -- place 2";
    validateResource(
      subjectEdge.getTarget(),
      List.of(ResourceTypeDictionary.fromUri(subjectType).get(), ResourceTypeDictionary.CONCEPT),
      Map.ofEntries(
        Map.entry("http://bibfra.me/vocab/marc/numeration", List.of("numeration")),
        Map.entry("http://bibfra.me/vocab/lite/name", List.of("name")),
        Map.entry("http://bibfra.me/vocab/marc/titles", List.of("title 1", "title 2")),
        Map.entry("http://bibfra.me/vocab/marc/attribution", List.of("attribution")),
        Map.entry("http://bibfra.me/vocab/lite/date", List.of("2024")),
        Map.entry("http://bibfra.me/vocab/lite/nameAlternative", List.of("name alternative")),
        Map.entry("http://bibfra.me/vocab/marc/chronologicalSubdivision", List.of("temporal 1", "temporal 2")),
        Map.entry("http://bibfra.me/vocab/marc/generalSubdivision", List.of("topic 1", "topic 2")),
        Map.entry("http://bibfra.me/vocab/lite/label", List.of(expectedConceptLabel)),
        Map.entry("http://bibfra.me/vocab/marc/geographicSubdivision", List.of("place 1", "place 2")),
        Map.entry("http://bibfra.me/vocab/marc/formSubdivision", List.of("form 1", "form 2")),
        Map.entry("http://bibfra.me/vocab/marc/relator_term", List.of("relator"))
      ),
      expectedConceptLabel);

    // Validate focus resource
    var focusEdge = getFirstOutgoingEdge(subjectEdge, withPredicateUri(FOCUS.getUri()));
    validateResource(
      focusEdge.getTarget(),
      List.of(ResourceTypeDictionary.fromUri(subjectType).get()),
      Map.of(
        "http://bibfra.me/vocab/marc/numeration", List.of("numeration"),
        "http://bibfra.me/vocab/lite/name", List.of("name"),
        "http://bibfra.me/vocab/marc/titles", List.of("title 1", "title 2"),
        "http://bibfra.me/vocab/marc/attribution", List.of("attribution"),
        "http://bibfra.me/vocab/lite/date", List.of("2024"),
        "http://bibfra.me/vocab/lite/nameAlternative", List.of("name alternative")
      ),
      "numeration, name, title 1, title 2, name alternative, 2024"
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
        validateResource(r, List.of(properties.type()), properties.properties(), label);
      });
  }
}
