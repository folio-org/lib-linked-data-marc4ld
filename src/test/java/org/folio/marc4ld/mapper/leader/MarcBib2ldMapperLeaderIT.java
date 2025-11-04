package org.folio.marc4ld.mapper.leader;

import static org.assertj.core.api.Assertions.assertThat;

import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.configuration.Marc4LdObjectMapper;
import org.folio.marc4ld.service.marc2ld.bib.MarcBib2ldMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MarcBib2ldMapperLeaderIT extends Marc2LdTestBase {

  @Autowired
  private MarcBib2ldMapper marcBib2ldMapper;

  @Autowired
  private Marc4LdObjectMapper objectMapper;

  @Test
  void shouldProcessBook() throws Exception {
    // given
    var leader = "      aa                ";

    // when
    var resource = marcBib2ldMapper.fromMarcJson(objectMapper.writeValueAsString(new MarcJson(leader)));

    // then
    assertThat(resource).isPresent();
  }

  @Test
  void shouldProcessSerial() throws Exception {
    // given
    var leader = "       s                ";

    // when
    var resource = marcBib2ldMapper.fromMarcJson(objectMapper.writeValueAsString(new MarcJson(leader)));

    // then
    assertThat(resource).isPresent();
  }

  @Test
  void shouldNotProcessUnknownType() throws Exception {
    // given
    var leader = "                        ";

    // when
    var resource = marcBib2ldMapper.fromMarcJson(objectMapper.writeValueAsString(new MarcJson(leader)));

    // then
    assertThat(resource).isNotPresent();
  }

  private record MarcJson(String leader) {
  }
}
