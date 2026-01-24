package org.folio.marc4ld.mapper.field600;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
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

class Marc2Ld600HubIT extends Marc2LdTestBase {

  @ParameterizedTest
  @CsvSource(value = {
    "fields/600/marc_600_person_hub.jsonl, http://bibfra.me/vocab/lite/Person",
    "fields/600/marc_600_family_hub.jsonl, http://bibfra.me/vocab/lite/Family"
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
    var hubLabel = "Dracontius, Blossius Aemilius. active 5th century. Part II. Liber 2. Medea. German & Latin";
    var conceptLabel = hubLabel + " -- Drama -- Parodies, imitations, etc -- 1st Century -- Spain -- Germany";
    validateResource(
      subjectEdge.getTarget(),
      List.of(HUB, CONCEPT),
      Map.of(
        "http://bibfra.me/vocab/library/formSubdivision", List.of("Drama"),
        "http://bibfra.me/vocab/lite/language", List.of("German & Latin"),
        "http://bibfra.me/vocab/library/generalSubdivision", List.of("Parodies, imitations, etc"),
        "http://bibfra.me/vocab/lite/label", List.of(conceptLabel),
        "http://bibfra.me/vocab/lite/name", List.of(hubLabel),
        "http://bibfra.me/vocab/library/geographicSubdivision", List.of("Spain", "Germany"),
        "http://bibfra.me/vocab/library/chronologicalSubdivision", List.of("1st Century")
      ),
      conceptLabel);

    // Validate focus resource
    var focusEdge = getFirstOutgoingEdge(subjectEdge, withPredicateUri(FOCUS.getUri()));
    validateResource(
      focusEdge.getTarget(),
      List.of(HUB),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of(hubLabel),
        "http://bibfra.me/vocab/lite/language", List.of("German & Latin")
      ),
      hubLabel
    );

    // validate title resource
    var titleEdge = getFirstOutgoingEdge(focusEdge, withPredicateUri(TITLE.getUri()));
    validateResource(
      titleEdge.getTarget(),
      List.of(ResourceTypeDictionary.TITLE),
      Map.of(
        "http://bibfra.me/vocab/library/mainTitle", List.of("Medea"),
        "http://bibfra.me/vocab/library/partNumber", List.of("Liber 2"),
        "http://bibfra.me/vocab/library/partName", List.of("Part II")
      ),
      "Medea Liber 2 Part II"
    );

    // validate creator resource
    var expectedCreatorLabel = "Dracontius, Blossius Aemilius, active 5th century";
    var creatorEdge = getFirstOutgoingEdge(focusEdge, withPredicateUri(CREATOR.getUri()));
    validateResource(
      creatorEdge.getTarget(),
      List.of(ResourceTypeDictionary.fromUri(subjectType).get()),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("Dracontius, Blossius Aemilius"),
        "http://bibfra.me/vocab/lite/date", List.of("active 5th century"),
        "http://bibfra.me/vocab/lite/label", List.of(expectedCreatorLabel)
      ),
      expectedCreatorLabel
    );

    // Validate subFocus resources
    var subFocusEdges = getOutgoingEdges(subjectEdge, withPredicateUri(SUB_FOCUS.getUri()));
    assertThat(subFocusEdges).hasSize(5);
    var expectedSubFocuses = Map.of(
      "Drama", new TypeAndProperties(FORM,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("Drama"),
          "http://bibfra.me/vocab/lite/name", List.of("Drama"))
      ),
      "Parodies, imitations, etc", new TypeAndProperties(TOPIC,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("Parodies, imitations, etc"),
          "http://bibfra.me/vocab/lite/name", List.of("Parodies, imitations, etc"))
      ),
      "1st Century", new TypeAndProperties(TEMPORAL,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("1st Century"),
          "http://bibfra.me/vocab/lite/name", List.of("1st Century"))
      ),
      "Spain", new TypeAndProperties(PLACE,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("Spain"),
          "http://bibfra.me/vocab/lite/name", List.of("Spain"))
      ),
      "Germany", new TypeAndProperties(PLACE,
        Map.of(
          "http://bibfra.me/vocab/lite/label", List.of("Germany"),
          "http://bibfra.me/vocab/lite/name", List.of("Germany"))
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
