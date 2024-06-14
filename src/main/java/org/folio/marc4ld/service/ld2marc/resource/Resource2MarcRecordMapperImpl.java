package org.folio.marc4ld.service.ld2marc.resource;

import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.S;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.field.Bibframe2MarcFieldRuleApplier;
import org.folio.marc4ld.service.ld2marc.mapper.Ld2MarcMapper;
import org.folio.marc4ld.service.ld2marc.processing.DataFieldPostProcessorFactory;
import org.folio.marc4ld.service.ld2marc.resource.field.ControlFieldsBuilder;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Resource2MarcRecordMapperImpl implements Resource2MarcRecordMapper {

  private final MarcFactory marcFactory;
  private final Collection<Bibframe2MarcFieldRuleApplier> rules;
  private final List<Ld2MarcMapper> ld2MarcMappers;
  private final Comparator<Subfield> subfieldComparator;
  private final DataFieldPostProcessorFactory dataFieldPostProcessorFactory;

  @Override
  public Record toMarcRecord(Resource resource) {
    var marcRecord = marcFactory.newRecord();
    var cfb = new ControlFieldsBuilder();
    var dataFields = getFields(new ResourceEdge(null, resource, null), cfb);
    Stream.concat(cfb.build(marcFactory), dataFields.stream())
      .sorted(comparing(VariableField::getTag))
      .forEach(marcRecord::addVariableField);
    addInternalIds(marcRecord, resource);
    return marcRecord;
  }

  private List<DataField> getFields(ResourceEdge edge, ControlFieldsBuilder cfb) {
    return ld2MarcMappers.stream()
      .filter(mapper -> mapper.canMap(edge.getPredicate(), edge.getTarget()))
      .findFirst()
      .map(mapper -> mapper.map(edge.getTarget()))
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
    Bibframe2MarcFieldRuleApplier bibframe2MarcFieldRuleApplier
  ) {
    var resource = edge.getTarget();
    return Optional.of(bibframe2MarcFieldRuleApplier)
      .filter(b2mRule -> b2mRule.isSuitable(edge))
      .map(b2mRule -> {
        addControlFieldsToBuilder(b2mRule, resource, cfb);
        return b2mRule;
      })
      .filter(b2mRule -> b2mRule.isDataFieldCreatable(resource))
      .map(b2mRule -> getDataField(b2mRule, resource))
      .stream();
  }

  private void addControlFieldsToBuilder(Bibframe2MarcFieldRuleApplier rule, Resource res, ControlFieldsBuilder cfb) {
    rule.getControlFields(res)
      .forEach(field -> cfb.addFieldValue(field.tag(), field.value(), field.startPosition(), field.endPosition()));
  }

  private DataField getDataField(Bibframe2MarcFieldRuleApplier b2mRule, Resource resource) {
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
    if (nonNull(resource.getInventoryId()) || nonNull(resource.getSrsId())) {
      var field999 = marcFactory.newDataField(FIELD_UUID, SPACE, SPACE);
      ofNullable(resource.getInventoryId()).ifPresent(
        id -> field999.addSubfield(marcFactory.newSubfield(SUBFIELD_INVENTORY_ID, id.toString())));
      ofNullable(resource.getSrsId()).ifPresent(
        id -> field999.addSubfield(marcFactory.newSubfield(S, id.toString())));
      marcRecord.addVariableField(field999);
    }
  }
}
