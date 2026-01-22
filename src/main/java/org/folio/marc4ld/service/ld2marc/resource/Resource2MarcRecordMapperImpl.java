package org.folio.marc4ld.service.ld2marc.resource;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Stream.concat;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.ObjectUtils.allNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.INDICATOR_FOLIO;
import static org.folio.marc4ld.util.Constants.S;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;
import static org.folio.marc4ld.util.MarcUtil.addSubfieldIfNotDuplicate;
import static org.folio.marc4ld.util.MarcUtil.orderSubfields;
import static org.folio.marc4ld.util.MarcUtil.sortFields;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
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
    sortFields(marcRecord);
    return marcRecord;
  }

  private Record buildMarcRecord(Resource resource) {
    var marcRecord = marcFactory.newRecord();
    var cfb = new ControlFieldsBuilder();
    var dataFields = getDataFields(new ResourceEdge(null, resource, null), cfb);
    customControlFieldMappers.forEach(mapper -> mapper.map(resource, cfb));
    concat(cfb.build(marcFactory), dataFields.stream())
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
    var combinedFields = combineFields(fieldsFromResource.toList(), fieldsFromEdges.toList());

    combinedFields = performAdditionalMappings(combinedFields, edge);
    return dataFieldPostProcessorFactory.get()
      .apply(combinedFields, resource.getTypes());
  }

  private List<DataField> combineFields(List<DataField> fieldsFromResource, List<DataField> fieldsFromEdges) {
    if (isMergeable(fieldsFromResource, fieldsFromEdges)) {
      var merged = mergeDataFields(concat(fieldsFromResource.stream(), fieldsFromEdges.stream()).toList());
      return List.of(merged);
    }
    return concat(fieldsFromResource.stream(), fieldsFromEdges.stream()).toList();
  }

  private boolean isMergeable(List<DataField> fields1, List<DataField> fields2) {
    if (fields1.isEmpty() || fields2.isEmpty()) {
      return false;
    }
    var marcField = fields1.getFirst().getTag();
    return fields1.stream().allMatch(f -> f.getTag().equals(marcField))
           && fields2.stream().allMatch(f -> f.getTag().equals(marcField));
  }

  private DataField mergeDataFields(List<DataField> fields) {
    var marcField = fields.getFirst().getTag();
    var ind1 = selectFirstNonBlankIndicator(fields, DataField::getIndicator1);
    var ind2 = selectFirstNonBlankIndicator(fields, DataField::getIndicator2);

    var mergedField = marcFactory.newDataField(marcField, ind1, ind2);
    fields.stream()
      .flatMap(f -> f.getSubfields().stream())
      .forEach(sf -> addSubfieldIfNotDuplicate(mergedField, sf));
    orderSubfields(mergedField, subfieldComparator);

    return mergedField;
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

  private char selectFirstNonBlankIndicator(List<DataField> fields, Function<DataField, Character> indicatorGetter) {
    return fields.stream()
      .map(indicatorGetter)
      .filter(i -> !isBlank(String.valueOf(i)))
      .findFirst()
      .orElse(indicatorGetter.apply(fields.getFirst()));
  }
}
