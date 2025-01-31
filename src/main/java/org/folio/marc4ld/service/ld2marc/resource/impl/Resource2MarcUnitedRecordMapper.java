package org.folio.marc4ld.service.ld2marc.resource.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.field.Ld2MarcFieldRuleApplier;
import org.folio.marc4ld.service.ld2marc.leader.LeaderGenerator;
import org.folio.marc4ld.service.ld2marc.mapper.Ld2MarcMapper;
import org.folio.marc4ld.service.ld2marc.mapper.custom.Ld2MarcCustomMapper;
import org.folio.marc4ld.service.ld2marc.processing.DataFieldPostProcessorFactory;
import org.folio.marc4ld.service.marc2ld.reader.MarcReaderProcessor;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component(Resource2MarcUnitedRecordMapper.NAME)
public class Resource2MarcUnitedRecordMapper extends AbstarctResource2MarcRecordMapper {

  public static final String NAME = "resource2MarcUnitedRecordMapper";

  private final LeaderGenerator leaderGenerator;

  public Resource2MarcUnitedRecordMapper(MarcFactory marcFactory,
                                         Collection<Ld2MarcFieldRuleApplier> rules,
                                         List<Ld2MarcMapper> ld2MarcMappers,
                                         List<Ld2MarcCustomMapper> customMappers,
                                         Comparator<Subfield> subfieldComparator,
                                         DataFieldPostProcessorFactory dataFieldPostProcessorFactory,
                                         MarcReaderProcessor marcReaderProcessor,
                                         LeaderGenerator leaderGenerator) {
    super(marcFactory, rules, ld2MarcMappers, customMappers, subfieldComparator, dataFieldPostProcessorFactory,
      marcReaderProcessor);
    this.leaderGenerator = leaderGenerator;
  }

  @Override
  public Record toMarcRecord(Resource resource) {
    var marcRecord = buildMarcRecord(resource);
    getUnmappedMarcRecord(resource).ifPresent(unmappedMarcRecord -> unmappedMarcRecord.getDataFields()
      .forEach(marcRecord::addVariableField));
    addInternalIds(marcRecord, resource);
    addUpdatedDateField(marcRecord, resource);
    sortFields(marcRecord);
    leaderGenerator.addLeader(marcRecord);
    return marcRecord;
  }
}
