package org.folio.marc4ld.service.marc2ld.mapper.custom.impl;

import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.Constants.TAG_100;
import static org.folio.marc4ld.util.Constants.THREE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.impl.ControlFieldImpl;
import org.marc4j.marc.impl.DataFieldImpl;
import org.marc4j.marc.impl.LeaderImpl;
import org.marc4j.marc.impl.RecordImpl;
import org.marc4j.marc.impl.SubfieldImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class RawDataMapperTest {

  @Mock
  MarcFactory marcFactory;

  @Mock
  Marc4LdRules marc4LdRules;

  @InjectMocks
  private RawDataMapper mapper;

  @Test
  void mapShouldMapRawData() {
    //given
    var marcRecord = new RecordImpl();
    marcRecord.setLeader(new LeaderImpl("01767cam a22003977i 4500"));
    var mappedDataField = new DataFieldImpl(TAG_100, THREE, SPACE);
    mappedDataField.addSubfield(new SubfieldImpl(A, "Family name"));
    var unmappedDataField = new DataFieldImpl("880", SPACE, SPACE);
    unmappedDataField.addSubfield(new SubfieldImpl(A, "unmapped data"));
    marcRecord.addVariableField(mappedDataField);
    marcRecord.addVariableField(unmappedDataField);
    marcRecord.addVariableField(new ControlFieldImpl(TAG_008, "190607t19992019af            000 1    "));
    var resource = new Resource();
    when(marcFactory.newRecord()).thenReturn(new RecordImpl());
    when(marc4LdRules.getBibFieldRules()).thenReturn(Map.of(TAG_008, List.of(), TAG_100, List.of()));
    var expectedRawData = loadResourceAsString("raw_data.jsonl");

    //when
    mapper.map(marcRecord, resource);

    //then
    assertEquals(expectedRawData, resource.getRawData());
  }
}
