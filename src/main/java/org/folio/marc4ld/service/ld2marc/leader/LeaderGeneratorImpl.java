package org.folio.marc4ld.service.ld2marc.leader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.marc4ld.service.ld2marc.leader.enums.BibliographLevel;
import org.folio.marc4ld.service.ld2marc.leader.enums.CharCodingScheme;
import org.folio.marc4ld.service.ld2marc.leader.enums.ControlType;
import org.folio.marc4ld.service.ld2marc.leader.enums.DescriptiveCatalogingForm;
import org.folio.marc4ld.service.ld2marc.leader.enums.EncodingLevel;
import org.folio.marc4ld.service.ld2marc.leader.enums.MultipartResourceRecordLevel;
import org.folio.marc4ld.service.ld2marc.leader.enums.RecordStatus;
import org.folio.marc4ld.service.ld2marc.leader.enums.RecordType;
import org.marc4j.MarcStreamWriter;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class LeaderGeneratorImpl implements LeaderGenerator {

  private static final int INDICATOR_COUNT = 2;
  private static final int SUBFIELD_CODE_LENGTH = 2;
  private static final char LENGTH_OF_THE_LENGTH_OF_FIELD_PORTION = '4';
  private static final char LENGTH_OF_THE_STARTING_CHARACTER_POSITION_PORTION = '5';
  private static final char LENGTH_OF_THE_IMPLEMENTATION_DEFINED_PORTION = '0';
  private static final char UNDEFINED = '0';
  private static final int LEADER_LENGTH = 24;
  private static final int DIRECTORY_ENTRY_TERMINATOR = 1;
  private static final int DIR_RECORD_SIZE = 12;

  private final MarcFactory marcFactory;

  @Override
  public void addLeader(Record marcRecord) {
    var leader = marcFactory.newLeader();
    // 5
    leader.setRecordStatus(RecordStatus.NEW.value);
    // 6
    leader.setTypeOfRecord(RecordType.LANGUAGE_MATERIAL.value);
    // 7-8
    leader.setImplDefined1(new char[] {
      BibliographLevel.MONOGRAPH_OR_ITEM.value,
      ControlType.NO_SPECIFIED_TYPE.value
    });
    // 9
    leader.setCharCodingScheme(CharCodingScheme.UCS_OR_UNICODE.value);
    // 10
    leader.setIndicatorCount(INDICATOR_COUNT);
    // 11
    leader.setSubfieldCodeLength(SUBFIELD_CODE_LENGTH);
    // 12-16
    leader.setBaseAddressOfData(calculateBaseAddressOfData(marcRecord));
    // 17-19
    leader.setImplDefined2(new char[] {
      EncodingLevel.UNKNOWN.value,
      DescriptiveCatalogingForm.ISBD_PUNCTUATION_OMITTED.value,
      MultipartResourceRecordLevel.NOT_SPECIFIED_OR_NOT_APPLICABLE.value
    });
    // 20-23
    leader.setEntryMap(new char[] {
      LENGTH_OF_THE_LENGTH_OF_FIELD_PORTION,
      LENGTH_OF_THE_STARTING_CHARACTER_POSITION_PORTION,
      LENGTH_OF_THE_IMPLEMENTATION_DEFINED_PORTION,
      UNDEFINED});
    marcRecord.setLeader(leader);
    // 0-4
    leader.setRecordLength(measureRecordLength(marcRecord));
  }

  private int calculateBaseAddressOfData(Record marcRecord) {
    var fieldsNumber = marcRecord.getVariableFields().size();
    return LEADER_LENGTH + DIRECTORY_ENTRY_TERMINATOR + (DIR_RECORD_SIZE * fieldsNumber);
  }

  private int measureRecordLength(Record marcRecord) {
    try (var baos = new ByteArrayOutputStream()) {
      var writer = new MarcStreamWriter(baos, StandardCharsets.UTF_8.name());
      writer.write(marcRecord);
      writer.close();
      return baos.size();
    } catch (IOException e) {
      log.error("Exception during measuring record length", e);
    }
    return 0;
  }
}
