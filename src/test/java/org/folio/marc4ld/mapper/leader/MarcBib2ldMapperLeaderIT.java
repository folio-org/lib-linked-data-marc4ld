package org.folio.marc4ld.mapper.leader;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Condition;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.service.ld2marc.leader.enums.BibliographLevel;
import org.folio.marc4ld.service.ld2marc.leader.enums.RecordType;
import org.folio.marc4ld.service.marc2ld.bib.MarcBib2ldMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

public class MarcBib2ldMapperLeaderIT extends Marc2LdTestBase {

  private static final String LEADER_TEMPLATE = "11111x%s%s x1111111xx 1111";
  private static final char UNKNOWN_CODE = 'x';

  @Autowired
  private MarcBib2ldMapper marcBib2ldMapper;

  @Autowired
  private ObjectMapper objectMapper;

  @ParameterizedTest
  @MethodSource("leaderSource")
  public void map_shouldProcessMonographsOnly(String leader,
                                              Condition<Optional<Resource>> condition) throws Exception {
    var resource = marcBib2ldMapper.fromMarcJson(objectMapper.writeValueAsString(new MarcJson(leader)));
    assertThat(resource).satisfies(condition);
  }

  private static Stream<Arguments> leaderSource() {
    return Stream.of(
      Arguments.of(
        leader(RecordType.LANGUAGE_MATERIAL.value, BibliographLevel.MONOGRAPH_OR_ITEM.value),
        new Condition<Optional<Resource>>(Optional::isPresent, "Resource is present")
      ),
      Arguments.of(
        leader(RecordType.LANGUAGE_MATERIAL.value, BibliographLevel.MONOGRAPHIC_COMPONENT_PART.value),
        new Condition<Optional<Resource>>(Optional::isPresent, "Resource is present")
      ),
      Arguments.of(
        leader(RecordType.LANGUAGE_MATERIAL.value, UNKNOWN_CODE),
        new Condition<Optional<Resource>>(Optional::isEmpty, "Resource is empty")
      ),
      Arguments.of(
        leader(UNKNOWN_CODE, BibliographLevel.MONOGRAPHIC_COMPONENT_PART.value),
        new Condition<Optional<Resource>>(Optional::isEmpty, "Resource is empty")
      ),
      Arguments.of(
        leader(UNKNOWN_CODE, BibliographLevel.MONOGRAPH_OR_ITEM.value),
        new Condition<Optional<Resource>>(Optional::isEmpty, "Resource is empty")
      )
    );
  }

  private static String leader(char recordType, char bibliographicLevel) {
    return format(LEADER_TEMPLATE, recordType, bibliographicLevel);
  }

  private record MarcJson(String leader) {}
}
