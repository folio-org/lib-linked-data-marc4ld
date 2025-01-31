package org.folio.marc4ld.service.ld2marc.impl;

import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.resource.Resource2MarcRecordMapper;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.marc4j.marc.Record;
import org.marc4j.marc.impl.LeaderImpl;
import org.marc4j.marc.impl.RecordImpl;

@UnitTest
class AbstractLd2MarcMapperTest {

  ObjectMapper objectMapper = mock(ObjectMapper.class);
  Resource2MarcRecordMapper resourceMapper = mock(Resource2MarcRecordMapper.class);
  AbstractLd2MarcMapper mapper = new TestLd2MarcMapper(objectMapper, resourceMapper);

  @Test
  void toMarcJson_shouldReturnMarcJson() throws JsonProcessingException {
    //given
    var expectedMarcJson = "prettyMarcJson";
    var resource = getResourceWithDocAndEdges().addType(INSTANCE);
    when(resourceMapper.toMarcRecord(resource)).thenReturn(getMarcRecord());
    var jsonObject = new Object();
    when(objectMapper.readValue(anyString(), eq(Object.class))).thenReturn(jsonObject);
    when(objectMapper.writeValueAsString(jsonObject)).thenReturn(expectedMarcJson);

    //expect
    assertEquals(expectedMarcJson, mapper.toMarcJson(resource));
  }

  static Stream<Arguments> resourceProvider() {
    return Stream.of(
      Arguments.of(getEmptyResource()),
      Arguments.of(getResourceWithDocAndEdges())
    );
  }

  @ParameterizedTest
  @MethodSource("resourceProvider")
  void toMarcJson_shouldReturnNull(Resource resource) {
    //expect
    assertNull(mapper.toMarcJson(resource));
  }

  @Test
  void toMarcJson_shouldReturnNull_whenJsonProcessingExceptionIsThrown() throws JsonProcessingException {
    //given
    var resource = getResourceWithDocAndEdges().addType(INSTANCE);
    when(resourceMapper.toMarcRecord(resource)).thenReturn(getMarcRecord());
    when(objectMapper.readValue(anyString(), eq(Object.class))).thenThrow(JsonProcessingException.class);

    //expect
    assertNull(mapper.toMarcJson(resource));
  }

  static Resource getEmptyResource() {
    return new Resource();
  }

  static Resource getResourceWithDocAndEdges() {
    var resource = getEmptyResource();
    return resource
      .setDoc(BooleanNode.TRUE)
      .setOutgoingEdges(new HashSet<>(List.of(new ResourceEdge(resource, getEmptyResource(), INSTANTIATES))));
  }

  static Record getMarcRecord() {
    var marcRecord = new RecordImpl();
    marcRecord.setLeader(new LeaderImpl("01767cam a22003977i 4500"));
    return marcRecord;
  }

  static class TestLd2MarcMapper extends AbstractLd2MarcMapper {

    TestLd2MarcMapper(ObjectMapper objectMapper, Resource2MarcRecordMapper resourceMapper) {
      super(objectMapper, resourceMapper);
    }
  }
}
