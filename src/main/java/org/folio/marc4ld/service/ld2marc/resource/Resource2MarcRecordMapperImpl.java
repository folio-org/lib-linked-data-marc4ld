package org.folio.marc4ld.service.ld2marc.resource;

import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;
import static org.folio.marc4ld.util.Constants.SUBFIELD_SRS_ID;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.field.Bibframe2MarcFieldRule;
import org.folio.marc4ld.service.ld2marc.mapper.Ld2MarcMapper;
import org.folio.marc4ld.service.ld2marc.processing.DataFieldPostProcessor;
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
  private final Collection<Bibframe2MarcFieldRule> rules;
  private final List<Ld2MarcMapper> ld2MarcMappers;
  private final DataFieldPostProcessor dataFieldPostProcessor;

  @Override
  public Record toMarcRecord(Resource resource) {
    var marcRecord = marcFactory.newRecord();
    var cfb = new ControlFieldsBuilder();
    var dataFields = getFields(resource, null, cfb);
    var combinedFields = dataFieldPostProcessor.apply(dataFields);
    Stream.concat(cfb.build(marcFactory), combinedFields.stream())
      .sorted(comparing(VariableField::getTag))
      .forEach(marcRecord::addVariableField);
    addInternalIds(marcRecord, resource);
    return marcRecord;
  }

  private List<DataField> getFields(Resource resource, PredicateDictionary predicate, ControlFieldsBuilder cfb) {
    var mapperOptional = ld2MarcMappers.stream()
      .filter(mapper -> mapper.canMap(predicate, resource))
      .findFirst();
    if (mapperOptional.isPresent()) {
      return mapperOptional.get().map(resource);
    } else {
      var dataFields = rules.stream()
        .flatMap(tagToRule -> mapToDataFields(resource, predicate, cfb, tagToRule));

      return Stream.concat(dataFields,
          resource.getOutgoingEdges()
            .stream()
            .flatMap(re -> getFields(re.getTarget(), re.getPredicate(), cfb).stream()))
        .toList();
    }
  }

  private Stream<DataField> mapToDataFields(
    Resource resource,
    PredicateDictionary predicate,
    ControlFieldsBuilder cfb,
    Bibframe2MarcFieldRule bibframe2MarcFieldRule
  ) {
    return Optional.of(bibframe2MarcFieldRule)
      .filter(b2mRule -> b2mRule.isSuitable(resource, predicate))
      .map(b2mRule -> {
        addControlFieldsToBuilder(b2mRule, resource, cfb);
        return b2mRule;
      })
      .filter(b2mRule -> b2mRule.isDataFieldCreatable(resource))
      .flatMap(b2mRule -> getDataField(b2mRule, resource))
      .stream();
  }

  private void addControlFieldsToBuilder(Bibframe2MarcFieldRule rule, Resource res, ControlFieldsBuilder cfb) {
    rule.getControlFields(res)
      .forEach(field -> cfb.addFieldValue(field.tag(), field.value(), field.startPosition(), field.endPosition()));
  }

  private Optional<DataField> getDataField(Bibframe2MarcFieldRule b2mRule, Resource resource) {
    var ind1 = b2mRule.getInd1(resource);
    var ind2 = b2mRule.getInd2(resource);
    var field = marcFactory.newDataField(b2mRule.getTag(), ind1, ind2);

    var subFields = b2mRule.getSubFields(resource);
    subFields.stream()
      .map(subField -> marcFactory.newSubfield(subField.tag(), subField.value()))
      .sorted(Comparator.comparingInt(Subfield::getCode))
      .forEach(field::addSubfield);
    return Optional.of(field)
      .filter(f -> CollectionUtils.isNotEmpty(field.getSubfields()));
  }

  private void addInternalIds(Record marcRecord, Resource resource) {
    if (nonNull(resource.getInventoryId()) || nonNull(resource.getSrsId())) {
      var field999 = marcFactory.newDataField(FIELD_UUID, SPACE.charAt(0), SPACE.charAt(0));
      ofNullable(resource.getInventoryId()).ifPresent(
        id -> field999.addSubfield(marcFactory.newSubfield(SUBFIELD_INVENTORY_ID, id.toString())));
      ofNullable(resource.getSrsId()).ifPresent(
        id -> field999.addSubfield(marcFactory.newSubfield(SUBFIELD_SRS_ID, id.toString())));
      marcRecord.addVariableField(field999);
    }
  }
}
