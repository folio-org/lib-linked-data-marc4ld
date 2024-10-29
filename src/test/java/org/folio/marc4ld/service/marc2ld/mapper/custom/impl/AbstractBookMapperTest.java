package org.folio.marc4ld.service.marc2ld.mapper.custom.impl;

import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.PredicateDictionary.NULL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.stream.Stream;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.mapper.MapperHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.marc4j.marc.Leader;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

class AbstractBookMapperTest {

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

    var category = workEdges.iterator().next().getTarget();
    assertEquals(Set.of(CATEGORY), category.getTypes());
    assertEquals(1, category.getOutgoingEdges().size());
    assertEquals(IS_DEFINED_BY, category.getOutgoingEdges().iterator().next().getPredicate());
    assertEquals(Set.of(CATEGORY_SET), category.getOutgoingEdges().iterator().next().getTarget().getTypes());
    assertEquals(0, category.getOutgoingEdges().iterator().next().getTarget().getOutgoingEdges().size());
  }

  static class TestBookMapper extends AbstractBookMapper {

    TestBookMapper(LabelService labelService, MapperHelper mapperHelper, FingerprintHashService hashService) {
      super(labelService, mapperHelper, hashService);
    }

    @Override
    protected int getStartIndex() {
      return 0;
    }

    @Override
    protected int getEndIndex() {
      return 2;
    }

    @Override
    protected boolean isSupportedCode(char code) {
      return 'a' == code;
    }

    @Override
    protected PredicateDictionary getPredicate() {
      return NULL;
    }

    @Override
    protected String getCategorySetLink() {
      return "";
    }

    @Override
    protected String getCategorySetLabel() {
      return "";
    }

    @Override
    protected String getLinkSuffix(char code) {
      return "";
    }

    @Override
    protected String getTerm(char code) {
      return "";
    }

    @Override
    protected String getCode(char code) {
      return "";
    }
  }
}
