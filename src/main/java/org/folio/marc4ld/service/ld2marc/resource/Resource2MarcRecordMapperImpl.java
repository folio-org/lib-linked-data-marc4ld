package org.folio.marc4ld.service.ld2marc.resource;

import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.ObjectUtils.allNull;
import static org.apache.commons.lang3.ObjectUtils.anyNull;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.INDICATOR_FOLIO;
import static org.folio.marc4ld.util.Constants.S;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;
import static org.folio.marc4ld.util.Constants.TAG_005;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.FolioMetadata;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.field.Ld2MarcFieldRuleApplier;
import org.folio.marc4ld.service.ld2marc.mapper.Ld2MarcMapper;
import org.folio.marc4ld.service.ld2marc.mapper.custom.Ld2MarcCustomMapper;
import org.folio.marc4ld.service.ld2marc.mapper.custom.Ld2MarcCustomMapper.Context;
import org.folio.marc4ld.service.ld2marc.processing.DataFieldPostProcessorFactory;
import org.folio.marc4ld.service.ld2marc.resource.field.ControlFieldsBuilder;
import org.folio.marc4ld.util.LdUtil;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Resource2MarcRecordMapperImpl implements Resource2MarcRecordMapper {

  private static final SimpleDateFormat MARC_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss.0");

  private final MarcFactory marcFactory;
  private final Collection<Ld2MarcFieldRuleApplier> rules;
  private final List<Ld2MarcMapper> ld2MarcMappers;
  private final List<Ld2MarcCustomMapper> customMappers;
  private final Comparator<Subfield> subfieldComparator;
  private final DataFieldPostProcessorFactory dataFieldPostProcessorFactory;

  @Override
  public Record toMarcRecord(Resource resource) {
    var marcRecord = marcFactory.newRecord();
    var cfb = new ControlFieldsBuilder();
    var dataFields = getFields(new ResourceEdge(null, resource, null), cfb);
    var context = new Context(cfb, dataFields);
    customMappers.forEach(mapper -> mapper.map(resource, context));
    Stream.concat(cfb.build(marcFactory), dataFields.stream())
      .sorted(comparing(VariableField::getTag))
      .forEach(marcRecord::addVariableField);
    addInternalIds(marcRecord, resource);
    addDateFields(marcRecord, resource);
    return marcRecord;
  }

  private List<DataField> getFields(ResourceEdge edge, ControlFieldsBuilder cfb) {
    return ld2MarcMappers.stream()
      .map(mapper -> mapper.map(edge))
      .flatMap(Optional::stream)
      .findFirst()
      .map(List::of)
      .orElseGet(() -> getFields(cfb, edge))
      .stream()
      .filter(df -> isNotEmpty(df.getSubfields()))
      .toList();
  }

  private List<DataField> getFields(ControlFieldsBuilder cfb, ResourceEdge edge) {
    var resource = edge.getTarget();
    var fieldsFromResource = rules.stream()
      .flatMap(tagToRule -> mapToDataFields(edge, cfb, tagToRule));
    var fieldsFromEdges = getOutgoingEdges(cfb, resource);
    var combinedFields = Stream.concat(fieldsFromResource, fieldsFromEdges).toList();

    return dataFieldPostProcessorFactory.get()
      .apply(combinedFields, resource.getTypes());
  }

  private Stream<DataField> getOutgoingEdges(ControlFieldsBuilder cfb, Resource resource) {
    return resource.getOutgoingEdges()
      .stream()
      .flatMap(re -> getFields(re, cfb).stream());
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

  private void addDateFields(Record marcRecord, Resource resource) {
    addUpdatedDateField(marcRecord, resource);
  }

  private void addUpdatedDateField(Record marcRecord, Resource resource) {
    if (resource.getTypes().equals(Set.of(INSTANCE))) {
      var optionalWork = LdUtil.getWork(resource);
      optionalWork.ifPresentOrElse(work -> {
          var optionalDate = chooseDate(resource.getUpdatedAt(), work.getUpdatedAt());
          optionalDate.ifPresent(date -> addUpdatedDateField(marcRecord, date));
        },
        () -> ofNullable(resource.getUpdatedAt())
          .ifPresent(date -> addUpdatedDateField(marcRecord, date))
      );
    }
  }

  private void addUpdatedDateField(Record record, Date date) {
    record.addVariableField(marcFactory.newControlField(TAG_005, convertDate(date)));
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
    return MARC_DATE_FORMAT.format(date);
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
