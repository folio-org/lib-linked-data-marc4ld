package org.folio.marc4ld.service.condition;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@UnitTest
class Marc2LdConditionCheckerImplTest {

  static Stream<Arguments> substring() {
    return Stream.of(
        Arguments.of("abcd", null, "abcd"),
        Arguments.of("abcd", List.of(), "abcd"),
        Arguments.of("abcd", List.of(0, 1), "a"),
        Arguments.of("abcd", List.of(2, 3), "c"),
        Arguments.of("abcd", List.of(1, 5), "bcd"),
        Arguments.of("abcd", List.of(4, 5), ""),
        Arguments.of("abcd", List.of(5, 6), "")
    );
  }

  @ParameterizedTest
  @MethodSource
  void substring(String data, List<Integer> substring, String expected) {
    assertThat(Marc2LdConditionCheckerImpl.substring(data, substring)).isEqualTo(expected);
  }

}
