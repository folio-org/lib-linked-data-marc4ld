package org.folio.marc4ld.service.ld2marc.resource.impl;

import static org.folio.marc4ld.util.Constants.SPACE;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.field.Ld2MarcFieldRuleApplier;
import org.folio.marc4ld.service.ld2marc.mapper.Ld2MarcMapper;
import org.folio.marc4ld.service.ld2marc.mapper.custom.Ld2MarcCustomMapper;
import org.folio.marc4ld.service.ld2marc.processing.DataFieldPostProcessorFactory;
import org.folio.marc4ld.service.ld2marc.resource.Resource2MarcRecordMapper;
import org.folio.marc4ld.service.marc2ld.reader.MarcReaderProcessor;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(Resource2MarcDividedRecordMapper.NAME)
public class Resource2MarcDividedRecordMapper extends AbstarctResource2MarcRecordMapper {

  public static final String NAME = "resource2MarcDividedRecordMapper";

  private final Resource2MarcRecordMapper resource2MarcRecordMapper;

  public Resource2MarcDividedRecordMapper(MarcFactory marcFactory,
                                          Collection<Ld2MarcFieldRuleApplier> rules,
                                          List<Ld2MarcMapper> ld2MarcMappers,
                                          List<Ld2MarcCustomMapper> customMappers,
                                          Comparator<Subfield> subfieldComparator,
                                          DataFieldPostProcessorFactory dataFieldPostProcessorFactory,
                                          MarcReaderProcessor marcReaderProcessor,
                                          @Qualifier(Resource2MarcUnitedRecordMapper.NAME)
                                          Resource2MarcRecordMapper resource2MarcRecordMapper) {
    super(marcFactory, rules, ld2MarcMappers, customMappers, subfieldComparator, dataFieldPostProcessorFactory,
      marcReaderProcessor);
    this.resource2MarcRecordMapper = resource2MarcRecordMapper;
  }

  @Override
  public Record toMarcRecord(Resource resource) {
    var marcRecord = buildMarcRecord(resource);
    addInternalIds(marcRecord, resource);
    addUpdatedDateField(marcRecord, resource);
    sortFields(marcRecord);
    getUnmappedMarcRecord(resource).ifPresent(unmappedMarcRecord -> {
      marcRecord.addVariableField(marcFactory.newDataField("000", SPACE, SPACE));
      sortFields(unmappedMarcRecord);
      unmappedMarcRecord.getDataFields().forEach(marcRecord::addVariableField);
    });
    marcRecord.setLeader(resource2MarcRecordMapper.toMarcRecord(resource).getLeader());
    return marcRecord;
  }
}
