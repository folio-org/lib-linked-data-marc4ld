package org.folio.marc4ld.service.ld2marc.resource.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.TAG_005;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.Constants.TAG_043;
import static org.folio.marc4ld.util.Constants.TAG_245;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.folio.marc4ld.service.ld2marc.leader.LeaderGenerator;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@UnitTest
class Resource2MarcUnitedRecordMapperTest extends BaseResource2MarcRecordMapperTest {

  @Mock
  LeaderGenerator leaderGenerator;

  @InjectMocks
  private Resource2MarcUnitedRecordMapper mapper;

  @Test
  void toMarcRecord_shouldReturnUnitedMarcRecord_ifResourceHasUnmappedMarcRecord() {
    //given
    setupWithUnmappedRecord();

    //when
    var marcRecord = mapper.toMarcRecord(getResourceWithUnmappedMarc());

    //then
    assertThat(extractTags(marcRecord)).isEqualTo(List.of(TAG_005, TAG_008, TAG_043, TAG_245, FIELD_UUID));
    verify(leaderGenerator).addLeader(marcRecord);
  }

  @Test
  void toMarcRecord_shouldReturnSimpleMarcRecord_ifResourceDoesNotHaveUnmappedMarcRecord() {
    //given
    setupWithoutUnmappedRecord();

    //when
    var marcRecord = mapper.toMarcRecord(getResource());

    //then
    assertThat(extractTags(marcRecord)).isEqualTo(List.of(TAG_005, TAG_008, FIELD_UUID));
    verify(leaderGenerator).addLeader(marcRecord);
  }
}
