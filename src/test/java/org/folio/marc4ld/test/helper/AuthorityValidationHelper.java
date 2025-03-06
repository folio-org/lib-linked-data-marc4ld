package org.folio.marc4ld.test.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FORM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TEMPORAL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TOPIC;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getEdges;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.spring.testing.type.UnitTest;

@UnitTest
public class AuthorityValidationHelper {

  private static final Map<ResourceTypeDictionary, Character> FIELD_CODES = Map.of(
    FORM, 'v',
    TOPIC, 'x',
    TEMPORAL, 'y',
    PLACE, 'z'
  );

  public static void validateIdentifier(Resource resource, String expectedValue) {
    var resourceEdges = getEdges(resource, ID_LCCN, IDENTIFIER);
    assertThat(resourceEdges)
      .hasSize(1)
      .satisfies(edges ->
        validateEdge(resourceEdges.getFirst(), MAP, List.of(ID_LCCN, IDENTIFIER),
          Map.of(
            LINK.getValue(), List.of("http://id.loc.gov/authorities/" + expectedValue),
            NAME.getValue(), List.of(expectedValue),
            LABEL.getValue(), List.of(expectedValue)
          ), expectedValue));
  }

  public static void validateSubfocusResources(Resource resource) {
    FIELD_CODES.forEach((type, subfield) -> validateSubfocus(resource, type, subfield));
  }

  public static void validateFocusResource(Resource resource, ResourceTypeDictionary focusResourceType,
                                           Map<String, List<String>> properties, String label) {
    var focusEdges = getEdges(resource, focusResourceType);
    assertThat(focusEdges)
      .filteredOn(resourceEdge -> resourceEdge.getPredicate().equals(FOCUS))
      .singleElement()
      .extracting(ResourceEdge::getTarget)
      .satisfies(target -> validateResource(target, List.of(focusResourceType), properties, label));
  }

  public static void validateSubfocus(Resource resource, ResourceTypeDictionary type,
                                Character fieldCode) {
    assertNotNull(fieldCode);
    var resourceEdges = getEdges(resource, type);
    assertThat(resourceEdges)
      .filteredOn(resourceEdge -> resourceEdge.getPredicate().equals(SUB_FOCUS))
      .hasSize(2)
      .satisfies(edge -> validateEdge(edge.getFirst(), SUB_FOCUS, List.of(type),
        Map.of(
          LABEL.getValue(), List.of(fieldCode + "Value1"),
          NAME.getValue(), List.of(fieldCode + "Value1")
        ),
        fieldCode + "Value1"))
      .satisfies(edge -> validateEdge(edge.get(1), SUB_FOCUS, List.of(type),
        Map.of(
          LABEL.getValue(), List.of(fieldCode + "Value2"),
          NAME.getValue(), List.of(fieldCode + "Value2")
        ),
        fieldCode + "Value2"));
  }
}
