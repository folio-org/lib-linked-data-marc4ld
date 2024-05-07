package org.folio.marc4ld.service.marc2ld;

import static java.lang.Character.MIN_VALUE;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.util.BibframeUtil.getFirst;
import static org.folio.marc4ld.util.BibframeUtil.isNotEmpty;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;
import static org.folio.marc4ld.util.Constants.SUBFIELD_SRS_ID;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.condition.ConditionChecker;
import org.folio.marc4ld.service.marc2ld.field.FieldController;
import org.folio.marc4ld.service.marc2ld.preprocessor.FieldPreprocessor;
import org.marc4j.MarcJsonReader;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class Marc2BibframeMapperImpl implements Marc2BibframeMapper {

  private final Marc2ldRules rules;
  private final FieldController fieldController;
  private final FieldPreprocessor fieldPreprocessor;
  private final MarcFactory marcFactory;
  private final FingerprintHashService hashService;
  private final ConditionChecker conditionChecker;

  @Override
  public Resource fromMarcJson(String marc) {
    if (isEmpty(marc)) {
      log.warn("Given marc is empty [{}]", marc);
      return null;
    }
    var reader = getReader(marc);
    var instanceAndWork = createInstanceAndWork();
    while (reader.hasNext()) {
      fillInstanceFields(reader.next(), instanceAndWork.instance);
    }
    setAdditionInfo(instanceAndWork);
    cleanEmptyEdges(instanceAndWork.instance);
    return instanceAndWork.instance;
  }

  private void fillInstanceFields(org.marc4j.marc.Record marcRecord, Resource instance) {
    marcRecord.getDataFields()
      .forEach(dataField -> fillData(marcRecord, instance, dataField));
    marcRecord.getControlFields()
      .forEach(controlField -> fillControl(marcRecord, instance, controlField));
  }

  private void fillControl(org.marc4j.marc.Record marcRecord, Resource instance, ControlField controlField) {
    var dataField = marcFactory.newDataField(EMPTY, MIN_VALUE, MIN_VALUE);
    handleField(controlField.getTag(), instance, dataField, marcRecord);
  }

  private void fillData(org.marc4j.marc.Record marcRecord, Resource instance, DataField dataField) {
    handleField(dataField.getTag(), instance, dataField, marcRecord);
    if (FIELD_UUID.equals(dataField.getTag())) {
      instance.setInventoryId(readUuid(dataField.getSubfield(SUBFIELD_INVENTORY_ID)));
      instance.setSrsId(readUuid(dataField.getSubfield(SUBFIELD_SRS_ID)));
    }
  }

  private void handleField(String tag, Resource instance, DataField dataField, org.marc4j.marc.Record marcRecord) {
    fieldPreprocessor.apply(dataField)
      .ifPresent(field ->
        rules.findFiledRules(tag)
          .stream()
          .filter(rule -> conditionChecker
            .isMarc2LdConditionSatisfied(rule.getOriginal(), dataField, marcRecord.getControlFields()))
          .forEach(fr -> fieldController.handleField(instance, field, marcRecord.getControlFields(), fr))
      );
  }

  private UUID readUuid(Subfield subfield) {
    if (isNull(subfield) || isNull(subfield.getData())) {
      return null;
    }
    var value = subfield.getData().strip();
    try {
      return UUID.fromString(value);
    } catch (Exception e) {
      log.warn("Incorrect UUID value from Marc field 999, subfield [{}]: {}", subfield.getCode(), value);
      return null;
    }
  }

  private void cleanEmptyEdges(Resource resource) {
    var outEdges = resource.getOutgoingEdges().stream()
      .map(re -> {
        cleanEmptyEdges(re.getTarget());
        return re;
      })
      .filter(re -> isNotEmpty(re.getTarget()))
      .collect(Collectors.toCollection(LinkedHashSet::new));
    resource.setOutgoingEdges(outEdges);
  }

  private MarcJsonReader getReader(String marc) {
    return new MarcJsonReader(new ByteArrayInputStream(marc.getBytes(StandardCharsets.UTF_8)));
  }

  private InstanceAndWork createInstanceAndWork() {
    var work = new Resource().addType(WORK);
    var instance = new Resource().addType(INSTANCE);
    instance.getOutgoingEdges().add(new ResourceEdge(instance, work, INSTANTIATES));
    return new InstanceAndWork(instance, work);
  }

  private void setAdditionInfo(InstanceAndWork instanceAndWork) {
    setLabelAndId(instanceAndWork.instance);
    setLabelAndId(instanceAndWork.work);
  }

  private void setLabelAndId(Resource resource) {
    resource.setLabel(selectLabelFromTitle(resource));
    resource.setId(hashService.hash(resource));
  }

  private String selectLabelFromTitle(Resource resource) {
    var labels = resource.getOutgoingEdges().stream()
      .filter(e -> Objects.equals(TITLE.getUri(), e.getPredicate().getUri()))
      .map(re -> re.getTarget().getLabel())
      .toList();
    return getFirst(labels);
  }

  private record InstanceAndWork(Resource instance, Resource work) {
  }
}
