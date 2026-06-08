package org.folio.marc4ld.mapper.leader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONTINUING_RESOURCES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.JSON_MAPPER;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.enums.BibliographLevel;
import org.folio.marc4ld.enums.RecordType;
import org.folio.marc4ld.service.marc2ld.bib.MarcBib2ldMapper;
import org.folio.marc4ld.test.helper.ResourceEdgeHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

class MarcBib2ldMapperLeaderIT extends Marc2LdTestBase {

  private static final String LEADER_TEMPLATE = "      %s%s                ";

  @Autowired
  private MarcBib2ldMapper marcBib2ldMapper;

  @ParameterizedTest
  @MethodSource("booksLeaderSource")
  void shouldMapWorkTypeToBooks(String marc) {
    // when
    var resource = marcBibToResource(marc);

    // then
    assertThat(resource)
      .extracting(ResourceEdgeHelper::getWorkEdge)
      .satisfies(we -> validateResource(we.getTarget(), List.of(WORK, BOOKS), Map.of(), ""));
  }

  @ParameterizedTest
  @MethodSource("continuingResourcesLeaderSource")
  void shouldMapWorkTypeToContinuingResources(String marc) {
    // when
    var resource = marcBibToResource(marc);

    // then
    assertThat(resource)
      .extracting(ResourceEdgeHelper::getWorkEdge)
      .satisfies(we -> validateResource(we.getTarget(), List.of(WORK, CONTINUING_RESOURCES), Map.of(), ""));
  }

  @Test
  void shouldNotProcessUnknownType() {
    // given
    var leader = "                        ";

    // when
    var resource = marcBib2ldMapper.fromMarcJson(JSON_MAPPER.writeValueAsString(new MarcJson(leader)));

    // then
    assertThat(resource).isNotPresent();
  }

  @ParameterizedTest
  @MethodSource("nonMonographLeaderSource")
  void shouldNotProcess_nonMonographFormat(String leader) {
    // when
    var resource = marcBib2ldMapper.fromMarcJson(JSON_MAPPER.writeValueAsString(new MarcJson(leader)));

    // then
    assertThat(resource).isNotPresent();
  }

  private static Stream<String> booksLeaderSource() {
    return Stream.of(RecordType.LANGUAGE_MATERIAL, RecordType.MANUSCRIPT_LANGUAGE_MATERIAL)
      .flatMap(rt -> Stream.of(
          BibliographLevel.MONOGRAPH_OR_ITEM,
          BibliographLevel.MONOGRAPHIC_COMPONENT_PART,
          BibliographLevel.COLLECTION,
          BibliographLevel.SUBUNIT)
        .map(bl -> marcWithLanguage(LEADER_TEMPLATE.formatted(rt.value, bl.value))));
  }

  private static Stream<String> continuingResourcesLeaderSource() {
    return Stream.of(BibliographLevel.SERIAL, BibliographLevel.SERIAL_COMPONENT_PART)
      .map(bl -> marcWithLanguage(LEADER_TEMPLATE.formatted(' ', bl.value)));
  }

  private static String marcWithLanguage(String leader) {
    return """
      {
        "leader" : "%s",
        "fields" : [ {
          "008" : "                                   eng"
        } ]
      }""".formatted(leader);
  }

  private static Stream<String> nonMonographLeaderSource() {
    return Arrays.stream(RecordType.values())
      .filter(rt -> rt != RecordType.LANGUAGE_MATERIAL && rt != RecordType.MANUSCRIPT_LANGUAGE_MATERIAL)
      .map(rt -> LEADER_TEMPLATE.formatted(rt.value, BibliographLevel.MONOGRAPH_OR_ITEM.value));
  }

  private record MarcJson(String leader) {
  }
}
