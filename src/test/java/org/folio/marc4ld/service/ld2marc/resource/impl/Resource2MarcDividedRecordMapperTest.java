package org.folio.marc4ld.service.ld2marc.resource.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.TAG_005;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.Constants.TAG_043;
import static org.folio.marc4ld.util.Constants.TAG_245;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.List;
import org.folio.marc4ld.service.ld2marc.resource.Resource2MarcRecordMapper;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.marc4j.marc.Record;
import org.marc4j.marc.impl.DataFieldImpl;
import org.marc4j.marc.impl.LeaderImpl;
import org.marc4j.marc.impl.RecordImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@UnitTest
class Resource2MarcDividedRecordMapperTest extends BaseResource2MarcRecordMapperTest {

  @Mock
  Resource2MarcRecordMapper resource2MarcRecordMapper;

  @InjectMocks
  private Resource2MarcDividedRecordMapper mapper;

  private final String leader = "00103nam a2200049uc 4500";

  @Test
  void toMarcRecord_shouldReturnCombinedMarcRecordWithDividerDataField_ifResourceHasUnmappedMarcRecord() {
    //given
    setupWithUnmappedRecord();
    var dividerTag = "000";
    doReturn(new DataFieldImpl(dividerTag, SPACE, SPACE))
      .when(marcFactory).newDataField(dividerTag, SPACE, SPACE);
    var resource = getResourceWithUnmappedMarc();
    when(resource2MarcRecordMapper.toMarcRecord(resource)).thenReturn(getRecordWithLeader());

    //when
    var marcRecord = mapper.toMarcRecord(resource);

    //then
    assertThat(extractTags(marcRecord)).isEqualTo(List.of(TAG_005, TAG_008, FIELD_UUID, dividerTag, TAG_043, TAG_245));
    assertThat(marcRecord.getLeader().marshal()).isEqualTo(leader);
  }

  @Test
  void toMarcRecord_shouldReturnSimpleMarcRecord_ifResourceDoesNotHaveUnmappedMarcRecord() {
    //given
    setupWithoutUnmappedRecord();
    var resource = getResource();
    when(resource2MarcRecordMapper.toMarcRecord(resource)).thenReturn(getRecordWithLeader());

    //when
    var marcRecord = mapper.toMarcRecord(resource);

    //then
    assertThat(extractTags(marcRecord)).isEqualTo(List.of(TAG_005, TAG_008, FIELD_UUID));
    assertThat(marcRecord.getLeader().marshal()).isEqualTo(leader);
  }

  private Record getRecordWithLeader() {
    var marcRecord = new RecordImpl();
    marcRecord.setLeader(new LeaderImpl(leader));
    return marcRecord;
  }
}
