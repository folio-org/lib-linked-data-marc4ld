package org.folio.marc4ld.service.ld2marc.resource;

import static java.time.Instant.ofEpochMilli;
import static java.util.Objects.isNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.ObjectUtils.allNull;
import static org.apache.commons.lang3.ObjectUtils.anyNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.INDICATOR_FOLIO;
import static org.folio.marc4ld.util.Constants.S;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;
import static org.folio.marc4ld.util.Constants.TAG_005;
import static org.folio.marc4ld.util.LdUtil.getWork;
import static org.folio.marc4ld.util.LdUtil.isInstance;
import static org.folio.marc4ld.util.MarcUtil.sortFields;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.FolioMetadata;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.field.Ld2MarcFieldRuleApplier;
import org.folio.marc4ld.service.ld2marc.mapper.AdditionalDataFieldsMapper;
import org.folio.marc4ld.service.ld2marc.mapper.CustomControlFieldsMapper;
import org.folio.marc4ld.service.ld2marc.mapper.CustomDataFieldsMapper;
import org.folio.marc4ld.service.ld2marc.processing.DataFieldPostProcessorFactory;
import org.folio.marc4ld.service.ld2marc.resource.field.ControlFieldsBuilder;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Resource2MarcRecordMapperImpl implements Resource2MarcRecordMapper {

  private static final DateTimeFormatter MARC_UPDATED_DATE_FORMAT = DateTimeFormatter
    .ofPattern("yyyyMMddHHmmss.0")
    .withZone(ZoneOffset.UTC);

  private final MarcFactory marcFactory;
  private final Collection<Ld2MarcFieldRuleApplier> rules;
  private final List<CustomDataFieldsMapper> customDataFieldsMappers;
  private final List<CustomControlFieldsMapper> customControlFieldMappers;
  private final List<AdditionalDataFieldsMapper> additionalDataFieldsMappers;
  private final Comparator<Subfield> subfieldComparator;
  private final DataFieldPostProcessorFactory dataFieldPostProcessorFactory;

  @Override
  public Record toMarcRecord(Resource resource) {
    var marcRecord = buildMarcRecord(resource);
    addInternalIds(marcRecord, resource);
    addUpdatedDateField(marcRecord, resource);
    sortFields(marcRecord);
    return marcRecord;
  }

  private Record buildMarcRecord(Resource resource) {
    var marcRecord = marcFactory.newRecord();
    var cfb = new ControlFieldsBuilder();
    var dataFields = getDataFields(new ResourceEdge(null, resource, null), cfb);
    customControlFieldMappers.forEach(mapper -> mapper.map(resource, cfb));
    Stream.concat(cfb.build(marcFactory), dataFields.stream())
      .forEach(marcRecord::addVariableField);
    return marcRecord;
  }

  private List<DataField> getDataFields(ResourceEdge edge, ControlFieldsBuilder cfb) {
    return performCustomMapping(edge)
      .orElseGet(() -> performConfigBasedMapping(cfb, edge))
      .stream()
      .map(this::cleanEmptySubFields)
      .filter(df -> isNotEmpty(df.getSubfields()))
      .toList();
  }

  private DataField cleanEmptySubFields(DataField dataField) {
    dataField.getSubfields().removeIf(sf -> isBlank(sf.getData()));
    return dataField;
  }

  private Optional<List<DataField>> performCustomMapping(ResourceEdge edge) {
    return customDataFieldsMappers.stream()
      .map(mapper -> mapper.map(edge))
      .flatMap(Optional::stream)
      .findFirst()
      .map(List::of);
  }

  private List<DataField> performConfigBasedMapping(ControlFieldsBuilder cfb, ResourceEdge edge) {
    var resource = edge.getTarget();
    var fieldsFromResource = rules.stream()
      .flatMap(tagToRule -> mapToDataFields(edge, cfb, tagToRule));
    var fieldsFromEdges = getOutgoingEdges(cfb, resource);
    var combinedFields = Stream.concat(fieldsFromResource, fieldsFromEdges).toList();

    combinedFields = performAdditionalMappings(combinedFields, edge);
    return dataFieldPostProcessorFactory.get()
      .apply(combinedFields, resource.getTypes());
  }

  private List<DataField> performAdditionalMappings(List<DataField> mappedSoFar, ResourceEdge edge) {
    return mappedSoFar.stream()
      .map(dataField -> performAdditionalMappings(dataField, edge))
      .toList();
  }

  private DataField performAdditionalMappings(DataField mappedSoFar, ResourceEdge edge) {
    for (var additionalMapper : additionalDataFieldsMappers) {
      mappedSoFar = additionalMapper.map(edge, mappedSoFar);
    }
    return mappedSoFar;
  }

  private Stream<DataField> getOutgoingEdges(ControlFieldsBuilder cfb, Resource resource) {
    return resource.getOutgoingEdges()
      .stream()
      .flatMap(re -> getDataFields(re, cfb).stream());
  }

  private Stream<DataField> mapToDataFields(
    ResourceEdge edge,
    ControlFieldsBuilder cfb,
    Ld2MarcFieldRuleApplier ld2MarcFieldRuleApplier
  ) {
    var resource = edge.getTarget();
    return Optional.of(ld2MarcFieldRuleApplier)
      .filter(b2mRule -> b2mRule.isSuitable(edge))
      .map(b2mRule -> {
        addControlFieldsToBuilder(b2mRule, resource, cfb);
        return b2mRule;
      })
      .filter(b2mRule -> b2mRule.isDataFieldCreatable(resource))
      .map(b2mRule -> getDataField(b2mRule, resource))
      .stream();
  }

  private void addControlFieldsToBuilder(Ld2MarcFieldRuleApplier rule, Resource res, ControlFieldsBuilder cfb) {
    rule.getControlFields(res)
      .forEach(field -> cfb.addFieldValue(field.tag(), field.value(), field.startPosition(), field.endPosition()));
  }

  private DataField getDataField(Ld2MarcFieldRuleApplier b2mRule, Resource resource) {
    var ind1 = b2mRule.getInd1(resource);
    var ind2 = b2mRule.getInd2(resource);
    var field = marcFactory.newDataField(b2mRule.getTag(), ind1, ind2);

    var subFields = b2mRule.getSubFields(resource);
    subFields.stream()
      .map(subField -> marcFactory.newSubfield(subField.tag(), subField.value()))
      .sorted(subfieldComparator)
      .forEach(field::addSubfield);
    return field;
  }

  private void addUpdatedDateField(Record marcRecord, Resource resource) {
    if (isInstance(resource)) {
      var optionalWork = getWork(resource);
      optionalWork.ifPresentOrElse(work -> {
          var optionalDate = chooseDate(resource.getUpdatedDate(), work.getUpdatedDate());
          optionalDate.ifPresent(date -> addUpdatedDateField(marcRecord, date));
        },
        () -> ofNullable(resource.getUpdatedDate())
          .ifPresent(date -> addUpdatedDateField(marcRecord, date))
      );
    }
  }

  private void addUpdatedDateField(Record marcRecord, Date date) {
    marcRecord.addVariableField(marcFactory.newControlField(TAG_005, convertDate(date)));
  }

  private Optional<Date> chooseDate(Date instanceDate, Date workDate) {
    if (anyNull(instanceDate, workDate)) {
      return Stream.of(instanceDate, workDate)
        .filter(Objects::nonNull)
        .findFirst();
    }
    return instanceDate.after(workDate) ? of(instanceDate) : of(workDate);
  }

  private String convertDate(Date date) {
    return MARC_UPDATED_DATE_FORMAT.format(ofEpochMilli(date.getTime()));
  }

  private void addInternalIds(Record marcRecord, Resource resource) {
    var folioMetadata = resource.getFolioMetadata();
    if (notContainsMetadata(folioMetadata)) {
      return;
    }
    var field999 = marcFactory.newDataField(FIELD_UUID, INDICATOR_FOLIO, INDICATOR_FOLIO);
    ofNullable(folioMetadata.getInventoryId()).ifPresent(
      id -> field999.addSubfield(marcFactory.newSubfield(SUBFIELD_INVENTORY_ID, id)));
    ofNullable(folioMetadata.getSrsId()).ifPresent(
      id -> field999.addSubfield(marcFactory.newSubfield(S, id)));
    marcRecord.addVariableField(field999);
  }

  private boolean notContainsMetadata(FolioMetadata folioMetadata) {
    return isNull(folioMetadata) || allNull(folioMetadata.getInventoryId(), folioMetadata.getSrsId());
  }
}
