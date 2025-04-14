package org.folio.marc4ld.mapper.field110and710;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getWorkEdge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class Marc2Ld110And710IT extends Marc2LdTestBase {

  @ParameterizedTest
  @CsvSource(value = {
    "ORGANIZATION, CREATOR, fields/110_710/marc_110_organization.jsonl",
    "JURISDICTION, CREATOR, fields/110_710/marc_110_jurisdiction.jsonl",
    "ORGANIZATION, CONTRIBUTOR, fields/110_710/marc_710_organization.jsonl",
    "JURISDICTION, CONTRIBUTOR, fields/110_710/marc_710_jurisdiction.jsonl"
  })
  void shouldMapFields110And710(ResourceTypeDictionary resourceType, PredicateDictionary predicate, String marcFile) {
    // given
    var marc = loadResourceAsString(marcFile);

    // when
    var resource = marcBibToResource(marc);

    // then
    assertThat(resource).isNotNull();
    var work = getWorkEdge(resource).getTarget();
    assertThat(work)
      .satisfies(w -> validateResource(w, List.of(WORK), new HashMap<>(), ""))
      .satisfies(w -> validateEdges(w, resourceType, predicate));
  }

  private void validateEdges(Resource work, ResourceTypeDictionary resourceType, PredicateDictionary predicate) {
    assertThat(getEdge(work, resourceType))
      .isPresent()
      .get()
      .satisfies(edge -> validateEdge(edge, predicate, List.of(resourceType), getProperties(),
        "author, subordinate unit, another subordinate unit, place, another place, date, another date"));
  }

  private Map<String, List<String>> getProperties() {
    return Map.ofEntries(
      entry("http://bibfra.me/vocab/lite/authorityLink", List.of("lccn link", "another lccn link")),
      entry("http://bibfra.me/vocab/lite/equivalent", List.of("equivalent", "another equivalent")),
      entry("http://bibfra.me/vocab/marc/subordinateUnit", List.of("subordinate unit", "another subordinate unit")),
      entry("http://bibfra.me/vocab/marc/linkage", List.of("linkage")),
      entry("http://bibfra.me/vocab/marc/controlField", List.of("control field", "another control field")),
      entry("http://bibfra.me/vocab/marc/fieldLink", List.of("field link", "another field link")),
      entry("http://bibfra.me/vocab/lite/name", List.of("author")),
      entry("http://bibfra.me/vocab/lite/date", List.of("date")),
      entry("http://bibfra.me/vocab/marc/miscInfo", List.of("misc info", "another misc info")),
      entry("http://bibfra.me/vocab/marc/numberOfParts", List.of("number of parts", "another number of parts")),
      entry("http://bibfra.me/vocab/marc/place", List.of("place", "another place")),
      entry("http://bibfra.me/vocab/scholar/affiliation", List.of("affiliation")),
      entry("http://bibfra.me/vocab/lite/label", List.of("author, subordinate unit, another subordinate unit, place,"
        + " another place, date, another date"))
    );
  }
}
