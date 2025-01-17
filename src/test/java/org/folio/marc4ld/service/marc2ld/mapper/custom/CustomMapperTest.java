package org.folio.marc4ld.service.marc2ld.mapper.custom;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;

@UnitTest
class CustomMapperTest {

  CustomMapper mapper = (marcRecord, instance) -> {
    throw new UnsupportedOperationException();
  };

  static Stream<Arguments> applicableDataProvider() {
    return Stream.of(
      Arguments.of('a', new char[] {'a'}),
      Arguments.of('a', new char[] {'c'}),
      Arguments.of('a', new char[] {'d'}),
      Arguments.of('a', new char[] {'m'}),
      Arguments.of('t', new char[] {'a'}),
      Arguments.of('t', new char[] {'c'}),
      Arguments.of('t', new char[] {'d'}),
      Arguments.of('t', new char[] {'m'})
    );
  }

  @ParameterizedTest
  @MethodSource("applicableDataProvider")
  void isApplicable_shouldReturn_true(char typeOfRecord, char[] implementationDefinedValues) {
    //given
    var marcRecord = mock(org.marc4j.marc.Record.class);
    var leader = mock(Leader.class);
    when(marcRecord.getLeader()).thenReturn(leader);
    when(leader.getTypeOfRecord()).thenReturn(typeOfRecord);
    when(leader.getImplDefined1()).thenReturn(implementationDefinedValues);

    //expect
    assertTrue(mapper.isApplicable(marcRecord));
  }

  static Stream<Arguments> notApplicableDataProvider() {
    return Stream.of(
      Arguments.of('a', new char[] {'b'}),
      Arguments.of('A', new char[] {'c'}),
      Arguments.of('a', new char[] {'@'}),
      Arguments.of('1', new char[] {'m'})
    );
  }

  @ParameterizedTest
  @MethodSource("notApplicableDataProvider")
  void isApplicable_shouldReturn_false(char typeOfRecord, char[] implementationDefinedValues) {
    //given
    var marcRecord = mock(Record.class);
    var leader = mock(Leader.class);
    when(marcRecord.getLeader()).thenReturn(leader);
    when(leader.getTypeOfRecord()).thenReturn(typeOfRecord);
    when(leader.getImplDefined1()).thenReturn(implementationDefinedValues);

    //expect
    assertFalse(mapper.isApplicable(marcRecord));
  }
}
