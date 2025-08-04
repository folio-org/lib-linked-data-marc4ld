package org.folio.marc4ld.service.ld2marc;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.TestUtil.extractTags;
import static org.folio.marc4ld.enums.UnmappedMarcHandling.APPEND;
import static org.folio.marc4ld.enums.UnmappedMarcHandling.MERGE;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.TAG_005;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.Constants.TAG_245;
import static org.folio.marc4ld.util.Constants.TAG_775;
import static org.folio.marc4ld.util.Constants.TAG_776;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import org.folio.ld.dictionary.model.RawMarc;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.enums.UnmappedMarcHandling;
import org.folio.marc4ld.service.ld2marc.leader.LeaderGenerator;
import org.folio.marc4ld.service.ld2marc.resource.Resource2MarcRecordMapper;
import org.folio.marc4ld.service.marc2ld.reader.MarcReaderProcessor;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.marc4j.marc.Record;
import org.marc4j.marc.impl.ControlFieldImpl;
import org.marc4j.marc.impl.DataFieldImpl;
import org.marc4j.marc.impl.LeaderImpl;
import org.marc4j.marc.impl.RecordImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@UnitTest
class CustomDataFieldsMapperImplTest {

  private static final String RAW_MARC = "rawMarc";
  private static final String TAG_007 = "007";
  @Mock
  ObjectMapper objectMapper;
  @Mock
  LeaderGenerator leaderGenerator;
  @Mock
  Resource2MarcRecordMapper resourceMapper;
  @Mock
  MarcReaderProcessor marcReaderProcessor;
  @InjectMocks
  Ld2MarcMapperImpl mapper;
  @Captor
  private ArgumentCaptor<Record> recordCaptor;
  @Captor
  private ArgumentCaptor<Resource> resourceCaptor;

  static Stream<Arguments> dataProvider() {
    return Stream.of(
      Arguments.of(APPEND, List.of(TAG_005, TAG_008, TAG_007, TAG_245, FIELD_UUID, TAG_775, TAG_776)),
      Arguments.of(MERGE, List.of(TAG_005, TAG_007, TAG_008, TAG_245, TAG_775, TAG_776, FIELD_UUID))
    );
  }

  @ParameterizedTest
  @MethodSource("dataProvider")
  void toMarcJson_shouldReturnMarcJson(UnmappedMarcHandling marcHandling, List<String> expectedTags)
    throws JsonProcessingException {
    //given
    var expectedMarcJson = "prettyMarcJson";
    var resource = getResourceWithDocAndEdges().addType(INSTANCE);
    var marcRecord = getMarcRecord();
    when(marcReaderProcessor.readMarc(RAW_MARC)).thenReturn(Stream.of(getUnmappedMarcRecord()));
    when(resourceMapper.toMarcRecord(resource)).thenReturn(marcRecord);
    var jsonObject = new Object();
    when(objectMapper.readValue(anyString(), eq(Object.class))).thenReturn(jsonObject);
    when(objectMapper.writeValueAsString(jsonObject)).thenReturn(expectedMarcJson);

    //expect
    assertEquals(expectedMarcJson, mapper.toMarcJson(resource, marcHandling));
    verify(leaderGenerator).addLeader(recordCaptor.capture(), resourceCaptor.capture());
    assertThat(extractTags(recordCaptor.getValue())).isEqualTo(expectedTags);
    assertThat(resourceCaptor.getValue()).isEqualTo(resource);
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
      .setOutgoingEdges(new HashSet<>(List.of(new ResourceEdge(resource, getEmptyResource(), INSTANTIATES))))
      .setUnmappedMarc(new RawMarc().setContent(RAW_MARC));
  }

  static Record getMarcRecord() {
    var marcRecord = new RecordImpl();
    marcRecord.setLeader(new LeaderImpl("01767cam a22003977i 4500"));
    marcRecord.addVariableField(new ControlFieldImpl(TAG_008, EMPTY));
    marcRecord.addVariableField(new ControlFieldImpl(TAG_005, EMPTY));
    marcRecord.addVariableField(new DataFieldImpl(FIELD_UUID, SPACE, SPACE));
    marcRecord.addVariableField(new DataFieldImpl(TAG_245, SPACE, SPACE));
    return marcRecord;
  }

  static Record getUnmappedMarcRecord() {
    var marcRecord = new RecordImpl();
    marcRecord.setLeader(new LeaderImpl("01767cam a22003977i 4500"));
    marcRecord.addVariableField(new ControlFieldImpl(TAG_007, EMPTY));
    marcRecord.addVariableField(new DataFieldImpl(TAG_776, SPACE, SPACE));
    marcRecord.addVariableField(new DataFieldImpl(TAG_775, SPACE, SPACE));
    return marcRecord;
  }
}
