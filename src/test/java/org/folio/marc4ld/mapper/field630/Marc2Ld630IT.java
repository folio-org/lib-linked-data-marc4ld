package org.folio.marc4ld.mapper.field630;

import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FORM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TEMPORAL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TOPIC;
import static org.folio.marc4ld.mapper.test.TestUtil.OBJECT_MAPPER;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getOutgoingEdges;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getWorkEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld630IT extends Marc2LdTestBase {

  private final ObjectMapper objectMapper = OBJECT_MAPPER;

  @Test
  void shouldMapField630() throws JsonProcessingException {
    // given
    var marc = loadResourceAsString("fields/630/marc_630.jsonl");
    var marcNode = objectMapper.readTree(marc);
    var node630 = stream(marcNode.get("fields").spliterator(), false)
      .filter(field -> field.has("630"))
      .findFirst()
      .orElse(null);
    var expectedMarcKey = objectMapper.writeValueAsString(node630);

    // when
    var resource = marcBibToResource(marc);

    //then
    var work = getWorkEdge(resource).getTarget();
    var subjectEdge = getFirstOutgoingEdge(work, withPredicateUri(SUBJECT.getUri()));

    // validate Concept resource
    var expectedConceptName = "uniform title 2024 2025 misc info another misc info form subheading k English medium "
      + "part number arranged stmt for music part name music key version";

    var expectedConceptLabel = expectedConceptName + " -- form 1 -- form 2 -- topic 1 -- topic 2 "
      + "-- temporal 1 -- temporal 2 -- place 1 -- place 2";
    validateResource(
      subjectEdge.getTarget(),
      List.of(HUB, CONCEPT),
      Map.ofEntries(
        Map.entry("http://bibfra.me/vocab/library/chronologicalSubdivision", List.of("temporal 1", "temporal 2")),
        Map.entry("http://bibfra.me/vocab/lite/label", List.of(expectedConceptLabel)),
        Map.entry("http://bibfra.me/vocab/library/generalSubdivision", List.of("topic 1", "topic 2")),
        Map.entry("http://bibfra.me/vocab/library/geographicSubdivision", List.of("place 1", "place 2")),
        Map.entry("http://bibfra.me/vocab/library/formSubdivision", List.of("form 1", "form 2")),
        Map.entry("http://bibfra.me/vocab/lite/language", List.of("English")),
        Map.entry("http://bibfra.me/vocab/lite/date", List.of("2025")),
        Map.entry("http://bibfra.me/vocab/library/version", List.of("version")),
        Map.entry("http://bibfra.me/vocab/library/musicKey", List.of("music key")),
        Map.entry("http://bibfra.me/vocab/library/legalDate", List.of("2024")),
        Map.entry("http://bibfra.me/vocab/lite/name", List.of(expectedConceptName))
      ),
      expectedConceptLabel);

    // Validate Hub resource
    var hubResource = getFirstOutgoingEdge(subjectEdge, withPredicateUri(FOCUS.getUri())).getTarget();
    validateResource(
      hubResource,
      List.of(ResourceTypeDictionary.HUB),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of(expectedConceptName),
        "http://bibfra.me/vocab/lite/language", List.of("English"),
        "http://bibfra.me/vocab/lite/date", List.of("2025"),
        "http://bibfra.me/vocab/library/version", List.of("version"),
        "http://bibfra.me/vocab/library/musicKey", List.of("music key"),
        "http://bibfra.me/vocab/library/legalDate", List.of("2024"),
        "http://bibfra.me/vocab/bflc/marcKey", List.of(expectedMarcKey)
      ),
      expectedConceptName
    );

    // Validate Hub's title resource
    var titleResource = getFirstOutgoingEdge(hubResource, withPredicateUri(TITLE.getUri())).getTarget();
    validateResource(
      titleResource,
      List.of(ResourceTypeDictionary.TITLE),
      Map.of(
        "http://bibfra.me/vocab/library/partNumber", List.of("part number"),
        "http://bibfra.me/vocab/library/partName", List.of("part name"),
        "http://bibfra.me/vocab/library/mainTitle", List.of(expectedConceptName),
        "http://bibfra.me/vocab/bflc/nonSortNum", List.of("8")
      ),
      expectedConceptName
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
