package org.folio.marc4ld.mapper.field240;

import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.EXPRESSION_OF;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.marc4ld.mapper.test.TestUtil.OBJECT_MAPPER;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getWorkEdge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld240IT extends Marc2LdTestBase {

  private final ObjectMapper objectMapper = OBJECT_MAPPER;

  @Test
  void shouldMapMarc240ToHub() throws JsonProcessingException {
    // given
    var marc = loadResourceAsString("fields/240/marc_240.jsonl");

    var marcNode = objectMapper.readTree(marc);
    var node240 = stream(marcNode.get("fields").spliterator(), false)
      .filter(field -> field.has("240"))
      .findFirst()
      .orElse(null);
    var expectedMarcKey = objectMapper.writeValueAsString(node240);

    var expectedHubLabel = "d-1 a-2 g-3 f-4 k-6 l-7 m-8 n-9 o-10 p-11 r-12 r-13 s-14";
    var expectedHubProps = Map.of(
      "http://bibfra.me/vocab/lite/label", List.of(expectedHubLabel),
      "http://bibfra.me/vocab/library/legalDate", List.of("d-1"),
      "http://bibfra.me/vocab/lite/date", List.of("f-4"),
      "http://bibfra.me/vocab/lite/language", List.of("l-7"),
      "http://bibfra.me/vocab/library/musicKey", List.of("r-12 r-13"),
      "http://bibfra.me/vocab/library/version", List.of("s-14"),
      "http://bibfra.me/vocab/bflc/marcKey", List.of(expectedMarcKey)
    );
    var expectedTitleProps = Map.of(
      "http://bibfra.me/vocab/library/mainTitle", List.of(expectedHubLabel),
      "http://bibfra.me/vocab/library/partName", List.of("p-11"),
      "http://bibfra.me/vocab/library/partNumber", List.of("n-9"),
      "http://bibfra.me/vocab/bflc/nonSortNum", List.of("4")
    );

    // when
    var resource = marcBibToResource(marc);

    // then
    var work = getWorkEdge(resource).getTarget();
    var hub = getExpressionOfEdge(work).getTarget();
    assertThat(hub)
      .satisfies(h -> validateResource(h, List.of(HUB), expectedHubProps, expectedHubLabel))
      .extracting(this::getTitleEdge)
      .satisfies(
        h -> validateEdge(h, TITLE, List.of(ResourceTypeDictionary.TITLE), expectedTitleProps, expectedHubLabel)
      );
  }

  @Test
  void shouldMapMarc240And100ToHubWithCreator() {
    // given
    var marc = loadResourceAsString("fields/240/marc_240_with_110.jsonl");
    var expectedOrganizationLabel = "Creator organization";
    var expectedTitleLabel = "Uniform title";
    var expectedHubLabel = expectedOrganizationLabel + " " + expectedTitleLabel;
    var expectedHubProps = Map.of(
      "http://bibfra.me/vocab/lite/label", List.of(expectedHubLabel),
      "http://bibfra.me/vocab/bflc/marcKey", List.of("""
        {"240":{"subfields":[{"a":"Uniform title"}],"ind1":" ","ind2":" "}}""")
    );
    var expectedOrganizationProps = Map.of(
      "http://bibfra.me/vocab/lite/name", List.of(expectedOrganizationLabel),
      "http://bibfra.me/vocab/lite/label", List.of(expectedOrganizationLabel)
    );
    var expectedTitleProps = Map.of(
      "http://bibfra.me/vocab/library/mainTitle", List.of(expectedTitleLabel)
    );

    // when
    var resource = marcBibToResource(marc);

    // then
    var work = getWorkEdge(resource).getTarget();
    var hub = getExpressionOfEdge(work).getTarget();
    var workCreator = getCreatorEdge(work).getTarget();
    assertThat(hub)
      .satisfies(h -> {
        validateResource(h, List.of(HUB), expectedHubProps, expectedHubLabel);
        var title = getTitleEdge(h).getTarget();
        validateResource(title, List.of(ResourceTypeDictionary.TITLE), expectedTitleProps, expectedTitleLabel);
      })
      .extracting(this::getCreatorEdge)
      .satisfies(c -> {
        assertThat(c.getTarget()).isEqualTo(workCreator);
        validateEdge(c, CREATOR, List.of(ORGANIZATION), expectedOrganizationProps, expectedOrganizationLabel);
      });
  }

  private ResourceEdge getTitleEdge(Resource resource) {
    return getFirstOutgoingEdge(resource, e -> e.getPredicate().equals(TITLE));
  }

  private ResourceEdge getExpressionOfEdge(Resource resource) {
    return getFirstOutgoingEdge(resource, e -> e.getPredicate().equals(EXPRESSION_OF));
  }

  private ResourceEdge getCreatorEdge(Resource resource) {
    return getFirstOutgoingEdge(resource, e -> e.getPredicate().equals(CREATOR));
  }
}
