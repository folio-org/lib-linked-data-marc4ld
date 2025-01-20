package org.folio.marc4ld.service.marc2ld.mapper.custom.impl;

import static org.folio.marc4ld.util.Constants.TAG_005;
import static org.folio.marc4ld.util.Constants.TAG_775;
import static org.folio.marc4ld.util.Constants.TAG_776;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.ld.dictionary.model.RawMarc;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.service.marc2ld.mapper.custom.CustomMapper;
import org.marc4j.MarcJsonWriter;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class RawMarcMapper implements CustomMapper {

  private static final Set<String> SELECTED_TAGS = Set.of(TAG_775, TAG_776);
  private static final Set<String> EXCLUDED_TAGS = Set.of(TAG_005);

  private final MarcFactory marcFactory;
  private final Marc4LdRules marc4LdRules;

  @Override
  public boolean isApplicable(Record marcRecord) {
    return true;
  }

  @Override
  public void map(Record marcRecord, Resource instance) {
    var unprocessed = marcFactory.newRecord();
    marcRecord.getVariableFields()
      .stream()
      .filter(variableField -> notMapped(variableField) || isSelected(variableField))
      .filter(this::notExcluded)
      .forEach(unprocessed::addVariableField);
    unprocessed.setLeader(marcRecord.getLeader());
    try (var byteArrayOutputStream = new ByteArrayOutputStream()) {
      var writer = new MarcJsonWriter(byteArrayOutputStream);
      writer.write(unprocessed);
      writer.close();
      instance.setUnmappedMarc(new RawMarc().setContent(byteArrayOutputStream.toString()));
    } catch (IOException e) {
      log.error("Exception during marc to resource conversion", e);
    }
  }

  private boolean notMapped(VariableField variableField) {
    return !marc4LdRules.getBibFieldRules().containsKey(variableField.getTag());
  }

  private boolean isSelected(VariableField variableField) {
    return SELECTED_TAGS.contains(variableField.getTag());
  }

  private boolean notExcluded(VariableField variableField) {
    return !EXCLUDED_TAGS.contains(variableField.getTag());
  }
}
