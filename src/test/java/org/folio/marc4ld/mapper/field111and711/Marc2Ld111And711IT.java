package org.folio.marc4ld.mapper.field111and711;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.MEETING;
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
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class Marc2Ld111And711IT extends Marc2LdTestBase {

  @ParameterizedTest
  @CsvSource(value = {
    "CREATOR, fields/111_711/marc_111.jsonl",
    "CONTRIBUTOR, fields/111_711/marc_711.jsonl",
  })
  void shouldMapFields111And711(PredicateDictionary predicate, String marcFile) {
    // given
    var marc = loadResourceAsString(marcFile);

    // when
    var resource = marcBibToResource(marc);

    // then
    assertThat(resource).isNotNull();
    var work = getWorkEdge(resource).getTarget();
    assertThat(work)
      .satisfies(w -> validateResource(w, List.of(WORK, BOOKS), new HashMap<>(), ""))
      .satisfies(w -> validateEdges(w, predicate));
  }

  private void validateEdges(Resource work, PredicateDictionary predicate) {
    assertThat(getEdge(work, MEETING))
      .isPresent()
      .get()
      .satisfies(e -> validateEdge(e, predicate, List.of(MEETING), getMeetingProperties(),
        "name, date, another date, place, another place, subordinate unit, another subordinate unit"));
  }

  private Map<String, List<String>> getMeetingProperties() {
    return Map.ofEntries(
      entry("http://bibfra.me/vocab/lite/name", List.of("name")),
      entry("http://bibfra.me/vocab/lite/authorityLink", List.of("authority link", "another authority link")),
      entry("http://bibfra.me/vocab/marc/subordinateUnit", List.of("subordinate unit", "another subordinate unit")),
      entry("http://bibfra.me/vocab/marc/place", List.of("place", "another place")),
      entry("http://bibfra.me/vocab/lite/date", List.of("date", "another date")),
      entry("http://bibfra.me/vocab/marc/miscInfo", List.of("misc info", "another misc info")),
      entry("http://bibfra.me/vocab/marc/numberOfParts", List.of("number of parts",  "another number of parts")),
      entry("http://bibfra.me/vocab/scholar/affiliation", List.of("affiliation")),
      entry("http://bibfra.me/vocab/lite/equivalent", List.of("equivalent", "another equivalent")),
      entry("http://bibfra.me/vocab/marc/linkage", List.of("linkage")),
      entry("http://bibfra.me/vocab/marc/controlField", List.of("control field", "another control field")),
      entry("http://bibfra.me/vocab/marc/fieldLink", List.of("field link", "another field link")),
      entry("http://bibfra.me/vocab/lite/label", List.of("name, date, another date, place, another place, "
        + "subordinate unit, another subordinate unit"))
    );
  }
}
