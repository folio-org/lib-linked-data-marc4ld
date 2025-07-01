package org.folio.marc4ld.service.marc2ld.bib.mapper.custom;

import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.marc4j.marc.Leader;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

@UnitTest
class AbstractLd2MarcBookMapperTest {

  LabelService labelService = mock(LabelService.class);
  MapperHelper mapperHelper = mock(MapperHelper.class);
  FingerprintHashService hashService = mock(FingerprintHashService.class);
  AbstractBookMapper mapper = new TestBookMapper(labelService, mapperHelper, hashService);

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
    var marcRecord = mock(Record.class);
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

  @Test
  void map() {
    //given
    var marcFactory = MarcFactory.newInstance();
    var marcRecord = marcFactory.newRecord();
    marcRecord.addVariableField(marcFactory.newControlField("008", "ab"));
    var instance = new Resource();
    var work = new Resource();
    instance.addOutgoingEdge(new ResourceEdge(instance, work, INSTANTIATES));

    //when
    mapper.map(marcRecord, instance);

    //then
    var workEdges = work.getOutgoingEdges();
    assertEquals(1, workEdges.size());
    assertEquals(NULL, workEdges.iterator().next().getPredicate());

    var subResource = workEdges.iterator().next().getTarget();
    assertEquals(1L, subResource.getId());
  }

  static class TestBookMapper extends AbstractBookMapper {

    TestBookMapper(LabelService labelService,
                          MapperHelper mapperHelper,
                          FingerprintHashService hashService) {
      super(labelService, mapperHelper, hashService, 0, 2);
    }

    @Override
    protected boolean isSupportedCode(char code) {
      return 'a' == code;
    }

    @Override
    protected void addSubResource(Resource resource, char code) {
      resource.addOutgoingEdge(new ResourceEdge(resource, new Resource().setId(1L), NULL));
    }
  }
}
