package org.folio.marc4ld.service.ld2marc.resource.impl;

import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.INDICATOR_FOLIO;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;
import static org.folio.marc4ld.util.Constants.TAG_005;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.Constants.TAG_043;
import static org.folio.marc4ld.util.Constants.TAG_245;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.FolioMetadata;
import org.folio.ld.dictionary.model.RawMarc;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.field.Ld2MarcFieldRuleApplier;
import org.folio.marc4ld.service.ld2marc.mapper.Ld2MarcMapper;
import org.folio.marc4ld.service.ld2marc.mapper.custom.Ld2MarcCustomMapper;
import org.folio.marc4ld.service.ld2marc.processing.DataFieldPostProcessorFactory;
import org.folio.marc4ld.service.ld2marc.processing.DataFieldPostProcessorImpl;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombinerFactoryImpl;
import org.folio.marc4ld.service.marc2ld.reader.MarcReaderProcessor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.marc4j.marc.impl.ControlFieldImpl;
import org.marc4j.marc.impl.DataFieldImpl;
import org.marc4j.marc.impl.RecordImpl;
import org.marc4j.marc.impl.SubfieldImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S2187")
class BaseResource2MarcRecordMapperTest {

  @Mock
  MarcFactory marcFactory;
  @Mock
  Collection<Ld2MarcFieldRuleApplier> rules;
  @Mock
  List<Ld2MarcMapper> ld2MarcMappers;
  @Mock
  List<Ld2MarcCustomMapper> customMappers;
  @Mock
  Comparator<Subfield> subfieldComparator;
  @Mock
  DataFieldPostProcessorFactory dataFieldPostProcessorFactory;
  @Mock
  MarcReaderProcessor marcReaderProcessor;

  private final String marcString = "marc string";

  void setupWithUnmappedRecord() {
    setupWithoutUnmappedRecord();
    var unmappedMarcRecord = new RecordImpl();
    unmappedMarcRecord.addVariableField(new DataFieldImpl(TAG_245, SPACE, SPACE));
    unmappedMarcRecord.addVariableField(new DataFieldImpl(TAG_043, SPACE, SPACE));
    when(marcReaderProcessor.readMarc(marcString)).thenReturn(Stream.of(unmappedMarcRecord));
  }

  void setupWithoutUnmappedRecord() {
    when(marcFactory.newRecord()).thenReturn(new RecordImpl());
    when(dataFieldPostProcessorFactory.get())
      .thenReturn(new DataFieldPostProcessorImpl(new DataFieldCombinerFactoryImpl(List.of())));
    when(marcFactory.newControlField(eq(TAG_008), any())).thenReturn(new ControlFieldImpl(TAG_008));
    doReturn(new DataFieldImpl(FIELD_UUID, INDICATOR_FOLIO, INDICATOR_FOLIO))
      .when(marcFactory).newDataField(FIELD_UUID, INDICATOR_FOLIO, INDICATOR_FOLIO);
    when(marcFactory.newSubfield(eq(SUBFIELD_INVENTORY_ID), any())).thenReturn(new SubfieldImpl(SUBFIELD_INVENTORY_ID));
    when(marcFactory.newControlField(eq(TAG_005), any())).thenReturn(new ControlFieldImpl(TAG_005));
  }

  Resource getResourceWithUnmappedMarc() {
    return getResource().setUnmappedMarc(new RawMarc().setContent(marcString));
  }

  Resource getResource() {
    return new Resource()
      .addType(ResourceTypeDictionary.INSTANCE)
      .setFolioMetadata(new FolioMetadata().setInventoryId("inventoryId"))
      .setUpdatedDate(new Date(1577836800000L));
  }

  List<String> extractTags(Record marcRecord) {
    return marcRecord.getVariableFields()
      .stream()
      .map(VariableField::getTag)
      .toList();
  }
}
