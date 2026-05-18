package org.folio.marc4ld.mapper.leader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.enums.BibliographLevel.MONOGRAPH_OR_ITEM;
import static org.folio.marc4ld.mapper.test.TestUtil.JSON_MAPPER;

import java.util.Arrays;
import java.util.stream.Stream;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.enums.RecordType;
import org.folio.marc4ld.service.marc2ld.bib.MarcBib2ldMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

class MarcBib2ldMapperLeaderIT extends Marc2LdTestBase {

  private static final String LEADER_TEMPLATE = "      %s%s                ";

  @Autowired
  private MarcBib2ldMapper marcBib2ldMapper;

  @Test
  void shouldProcessBook() {
    // given
    var leader = "      aa                ";

    // when
    var resource = marcBib2ldMapper.fromMarcJson(JSON_MAPPER.writeValueAsString(new MarcJson(leader)));

    // then
    assertThat(resource).isPresent();
  }

  @Test
  void shouldProcessSerial() {
    // given
    var leader = "       s                ";

    // when
    var resource = marcBib2ldMapper.fromMarcJson(JSON_MAPPER.writeValueAsString(new MarcJson(leader)));

    // then
    assertThat(resource).isPresent();
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

  private static Stream<String> nonMonographLeaderSource() {
    return Arrays.stream(RecordType.values())
      .filter(rt -> rt != RecordType.LANGUAGE_MATERIAL && rt != RecordType.MANUSCRIPT_LANGUAGE_MATERIAL)
      .map(rt -> LEADER_TEMPLATE.formatted(rt.value, MONOGRAPH_OR_ITEM.value));
  }

  private record MarcJson(String leader) {
  }
}
