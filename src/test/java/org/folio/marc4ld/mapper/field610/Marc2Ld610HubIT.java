package org.folio.marc4ld.mapper.field610;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FORM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.ld.dictionary.ResourceTypeDictionary.JURISDICTION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TOPIC;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getOutgoingEdges;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getWorkEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld610HubIT extends Marc2LdTestBase {

  @Test
  void shouldMapMarc610ToHubJurisdiction() {
    // given
    var marc = loadResourceAsString("fields/610/marc_610_hub_jurisdiction.jsonl");

    // when
    var resource = marcBibToResource(marc);

    // then
    var work = getWorkEdge(resource).getTarget();
    var subjectEdge = getFirstOutgoingEdge(work, withPredicateUri("http://bibfra.me/vocab/lite/subject"));

    var hubLabel = "United States. Constitution. Preamble. 19th Amendment. English";
    var conceptLabel = hubLabel + " -- Juvenile literature -- History";

    // validate concept resource
    validateResource(
      subjectEdge.getTarget(),
      List.of(HUB, CONCEPT),
      Map.of(
        "http://bibfra.me/vocab/library/formSubdivision", List.of("Juvenile literature"),
        "http://bibfra.me/vocab/library/generalSubdivision", List.of("History"),
        "http://bibfra.me/vocab/lite/label", List.of(conceptLabel),
        "http://bibfra.me/vocab/lite/name", List.of(hubLabel),
        "http://bibfra.me/vocab/lite/language", List.of("English")
      ),
      conceptLabel);

    // validate hub resource
    var focusEdge = getFirstOutgoingEdge(subjectEdge, withPredicateUri("http://bibfra.me/vocab/lite/focus"));
    validateResource(
      focusEdge.getTarget(),
      List.of(HUB),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of(hubLabel),
        "http://bibfra.me/vocab/lite/language", List.of("English")
      ),
      hubLabel
    );

    // validate hub's creator
    var creatorEdge = getFirstOutgoingEdge(focusEdge, withPredicateUri("http://bibfra.me/vocab/lite/creator"));
    validateResource(
      creatorEdge.getTarget(),
      List.of(JURISDICTION),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("United States"),
        "http://bibfra.me/vocab/lite/name", List.of("United States")
      ),
      "United States"
    );

    // validate hub's title
    var titleEdge = getFirstOutgoingEdge(focusEdge, withPredicateUri("http://bibfra.me/vocab/library/title"));
    validateResource(
      titleEdge.getTarget(),
      List.of(TITLE),
      Map.of(
        "http://bibfra.me/vocab/library/mainTitle", List.of("Constitution"),
        "http://bibfra.me/vocab/library/partNumber", List.of("19th Amendment"),
        "http://bibfra.me/vocab/library/partName", List.of("Preamble")
      ),
      "Constitution 19th Amendment Preamble"
    );

    // validate sub focuses
    var subFocusEdges = getOutgoingEdges(subjectEdge, withPredicateUri("http://bibfra.me/vocab/lite/subFocus"));
    assertThat(subFocusEdges).hasSize(2);

    // validate congresses sub-focus
    var juvenileLiterature = subFocusEdges.stream()
      .filter(e -> e.getTarget().getLabel().equals("Juvenile literature"))
      .findFirst()
      .orElseThrow();

    validateResource(
      juvenileLiterature.getTarget(),
      List.of(FORM),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("Juvenile literature"),
        "http://bibfra.me/vocab/lite/name", List.of("Juvenile literature")
      ),
      "Juvenile literature"
    );

    // validate history sub-focus
    var history = subFocusEdges.stream()
      .filter(e -> e.getTarget().getLabel().equals("History"))
      .findFirst()
      .orElseThrow();

    validateResource(
      history.getTarget(),
      List.of(TOPIC),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("History"),
        "http://bibfra.me/vocab/lite/name", List.of("History")
      ),
      "History"
    );
  }

  @Test
  void shouldMapMarc610ToHubOrganization() {
    // given
    var marc = loadResourceAsString("fields/610/marc_610_hub_organization.jsonl");

    // when
    var resource = marcBibToResource(marc);

    // then
    var work = getWorkEdge(resource).getTarget();
    var subjectEdge = getFirstOutgoingEdge(work, withPredicateUri("http://bibfra.me/vocab/lite/subject"));

    // validate concept resource
    var hubLabel = "United Nations. General Assembly. Universal Declaration of Human Rights";
    var conceptLabel = hubLabel + " -- Congresses -- History";
    validateResource(
      subjectEdge.getTarget(),
      List.of(HUB, CONCEPT),
      Map.of(
        "http://bibfra.me/vocab/library/formSubdivision", List.of("Congresses"),
        "http://bibfra.me/vocab/library/generalSubdivision", List.of("History"),
        "http://bibfra.me/vocab/lite/label", List.of(conceptLabel),
        "http://bibfra.me/vocab/lite/name", List.of(hubLabel)
      ),
      conceptLabel);

    // validate hub resource
    var focusEdge = getFirstOutgoingEdge(subjectEdge, withPredicateUri("http://bibfra.me/vocab/lite/focus"));
    validateResource(
      focusEdge.getTarget(),
      List.of(HUB),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of(hubLabel)
      ),
      hubLabel
    );

    // validate hub's creator
    var creatorEdge = getFirstOutgoingEdge(focusEdge, withPredicateUri("http://bibfra.me/vocab/lite/creator"));
    validateResource(
      creatorEdge.getTarget(),
      List.of(ORGANIZATION),
      Map.of(
        "http://bibfra.me/vocab/library/subordinateUnit", List.of("General Assembly"),
        "http://bibfra.me/vocab/lite/label", List.of("United Nations, General Assembly"),
        "http://bibfra.me/vocab/lite/name", List.of("United Nations")
      ),
      "United Nations, General Assembly"
    );

    // validate hub's title
    var titleEdge = getFirstOutgoingEdge(focusEdge, withPredicateUri("http://bibfra.me/vocab/library/title"));
    validateResource(
      titleEdge.getTarget(),
      List.of(TITLE),
      Map.of(
        "http://bibfra.me/vocab/library/mainTitle", List.of("Universal Declaration of Human Rights")
      ),
      "Universal Declaration of Human Rights"
    );

    // validate sub focuses
    var subFocusEdges = getOutgoingEdges(subjectEdge, withPredicateUri("http://bibfra.me/vocab/lite/subFocus"));
    assertThat(subFocusEdges).hasSize(2);

    // validate congresses sub-focus
    var congresses = subFocusEdges.stream()
      .filter(e -> e.getTarget().getLabel().equals("Congresses"))
      .findFirst()
      .orElseThrow();

    validateResource(
      congresses.getTarget(),
      List.of(FORM),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("Congresses"),
        "http://bibfra.me/vocab/lite/name", List.of("Congresses")
      ),
      "Congresses"
    );

    // validate history sub-focus
    var history = subFocusEdges.stream()
      .filter(e -> e.getTarget().getLabel().equals("History"))
      .findFirst()
      .orElseThrow();

    validateResource(
      history.getTarget(),
      List.of(TOPIC),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("History"),
        "http://bibfra.me/vocab/lite/name", List.of("History")
      ),
      "History"
    );
  }
}
