package org.folio.marc4ld.util;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONTINUING_RESOURCES;
import static org.folio.marc4ld.enums.BibliographLevel.COLLECTION;
import static org.folio.marc4ld.enums.BibliographLevel.MONOGRAPHIC_COMPONENT_PART;
import static org.folio.marc4ld.enums.BibliographLevel.MONOGRAPH_OR_ITEM;
import static org.folio.marc4ld.enums.BibliographLevel.SERIAL;
import static org.folio.marc4ld.enums.BibliographLevel.SERIAL_COMPONENT_PART;
import static org.folio.marc4ld.enums.BibliographLevel.SUBUNIT;
import static org.folio.marc4ld.enums.RecordType.LANGUAGE_MATERIAL;
import static org.folio.marc4ld.enums.RecordType.MANUSCRIPT_LANGUAGE_MATERIAL;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.stream.Stream;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.marc4j.marc.Record;
import org.marc4j.marc.impl.LeaderImpl;

@UnitTest
class TypeUtilTest {
  private static final String LEADER_TEMPLATE = "      %s%s                ";
  private static final char UNKNOWN_CODE = '_';

  @ParameterizedTest
  @MethodSource("leaderSource")
  void map_shouldProcessBooksAndSerialsOnly(String leader, ResourceTypeDictionary expected) {
    // given
    var marcRecord = mock(Record.class);
    doReturn(new LeaderImpl(leader)).when(marcRecord).getLeader();

    // when
    var result = TypeUtil.getWorkType(marcRecord);

    // then
    assertThat(result).isEqualTo(ofNullable(expected));
  }

  private static Stream<Arguments> leaderSource() {
    return Stream.of(
      of(leader(LANGUAGE_MATERIAL.value, MONOGRAPHIC_COMPONENT_PART.value), BOOKS),
      of(leader(LANGUAGE_MATERIAL.value, COLLECTION.value), BOOKS),
      of(leader(LANGUAGE_MATERIAL.value, SUBUNIT.value), BOOKS),
      of(leader(LANGUAGE_MATERIAL.value, MONOGRAPH_OR_ITEM.value), BOOKS),
      of(leader(MANUSCRIPT_LANGUAGE_MATERIAL.value, MONOGRAPHIC_COMPONENT_PART.value), BOOKS),
      of(leader(MANUSCRIPT_LANGUAGE_MATERIAL.value, COLLECTION.value), BOOKS),
      of(leader(MANUSCRIPT_LANGUAGE_MATERIAL.value, SUBUNIT.value), BOOKS),
      of(leader(MANUSCRIPT_LANGUAGE_MATERIAL.value, MONOGRAPH_OR_ITEM.value), BOOKS),
      of(leader(UNKNOWN_CODE, SERIAL_COMPONENT_PART.value), CONTINUING_RESOURCES),
      of(leader(UNKNOWN_CODE, SERIAL.value), CONTINUING_RESOURCES),
      of(leader(LANGUAGE_MATERIAL.value, UNKNOWN_CODE), null),
      of(leader(MANUSCRIPT_LANGUAGE_MATERIAL.value, UNKNOWN_CODE), null),
      of(leader(UNKNOWN_CODE, MONOGRAPHIC_COMPONENT_PART.value), null),
      of(leader(UNKNOWN_CODE, COLLECTION.value), null),
      of(leader(UNKNOWN_CODE, SUBUNIT.value), null),
      of(leader(UNKNOWN_CODE, MONOGRAPH_OR_ITEM.value), null),
      of(leader(UNKNOWN_CODE, UNKNOWN_CODE), null
      )
    );
  }

  private static String leader(char recordType, char bibliographicLevel) {
    return LEADER_TEMPLATE.formatted(recordType, bibliographicLevel);
  }

}
