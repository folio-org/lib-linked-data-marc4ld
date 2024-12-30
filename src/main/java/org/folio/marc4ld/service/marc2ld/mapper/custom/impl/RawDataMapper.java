package org.folio.marc4ld.service.marc2ld.mapper.custom.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.service.marc2ld.mapper.custom.CustomMapper;
import org.marc4j.MarcJsonWriter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class RawDataMapper implements CustomMapper {

  private final MarcFactory marcFactory;
  private final Marc4LdRules marc4LdRules;

  @Override
  public void map(Record marcRecord, Resource instance) {
    var unprocessed = marcFactory.newRecord();
    marcRecord.getVariableFields()
      .stream()
      .filter(variableField -> variableField instanceof ControlField || notMapped(variableField))
      .forEach(unprocessed::addVariableField);
    unprocessed.setLeader(marcRecord.getLeader());
    try (var byteArrayOutputStream = new ByteArrayOutputStream()) {
      var writer = new MarcJsonWriter(byteArrayOutputStream);
      writer.write(unprocessed);
      writer.close();
      instance.setRawData(byteArrayOutputStream.toString());
    } catch (IOException e) {
      log.error("Exception during marc to resource conversion", e);
    }
  }

  private boolean notMapped(VariableField variableField) {
    return !marc4LdRules.getBibFieldRules().containsKey(variableField.getTag());
  }
}
