package org.folio.marc4ld.service.marc2ld.bib;

import static java.lang.Character.MIN_VALUE;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.ld.dictionary.model.ResourceSource.MARC;
import static org.folio.marc4ld.util.BibframeUtil.getFirst;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.S;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.ld.dictionary.model.InstanceMetadata;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.enums.BibliographLevel;
import org.folio.marc4ld.enums.RecordType;
import org.folio.marc4ld.service.condition.ConditionChecker;
import org.folio.marc4ld.service.marc2ld.Marc2ldRules;
import org.folio.marc4ld.service.marc2ld.field.ResourceProcessor;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor.PreprocessorContext;
import org.folio.marc4ld.service.marc2ld.preprocessor.FieldPreprocessor;
import org.folio.marc4ld.service.marc2ld.reader.MarcReaderProcessor;
import org.folio.marc4ld.service.marc2ld.relation.EmptyEdgesCleaner;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class MarcBib2LdMapperImpl implements MarcBib2ldMapper {

  private final Marc2ldRules rules;
  private final ResourceProcessor fieldController;
  private final FieldPreprocessor fieldPreprocessor;
  private final MarcFactory marcFactory;
  private final FingerprintHashService hashService;
  private final ConditionChecker conditionChecker;
  private final MarcReaderProcessor marcReaderProcessor;
  private final EmptyEdgesCleaner emptyEdgesCleaner;

  @Override
  public Optional<Resource> fromMarcJson(String marc) {
    if (isEmpty(marc)) {
      log.warn("Given marc is empty [{}]", marc);
      return Optional.empty();
    }
    var records = marcReaderProcessor.readMarc(marc).filter(this::isMonograph).toList();
    if (records.isEmpty()) {
      log.warn("Given marc is not monograph, skipping: [{}]", marc);
      return Optional.empty();
    }
    return Optional.of(createInstanceAndWorkResource(records));
  }

  private boolean isMonograph(Record record) {
    var leader = record.getLeader();
    if (isNull(leader)) {
      return false;
    }
    char typeOfRecord = leader.getTypeOfRecord();
    char bibliographicLevel = leader.getImplDefined1()[0];
    return isLanguageMaterial(typeOfRecord) && isMonographicComponentPartOrItem(bibliographicLevel);
  }

  private boolean isLanguageMaterial(char typeOfRecord) {
    return typeOfRecord == RecordType.LANGUAGE_MATERIAL.value;
  }

  private boolean isMonographicComponentPartOrItem(char bibliographicLevel) {
    return bibliographicLevel == BibliographLevel.MONOGRAPHIC_COMPONENT_PART.value
      || bibliographicLevel == BibliographLevel.MONOGRAPH_OR_ITEM.value;
  }

  private Resource createInstanceAndWorkResource(List<Record> records) {
    var instanceAndWork = createInstanceAndWork();
    records.forEach(marcRecord -> fillInstanceFields(marcRecord, instanceAndWork.instance));
    setAdditionInfo(instanceAndWork);
    emptyEdgesCleaner.apply(instanceAndWork.instance);
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
      instance.getInstanceMetadata()
        .setInventoryId(readSubfieldValue(dataField.getSubfield(SUBFIELD_INVENTORY_ID)))
        .setSrsId(readSubfieldValue(dataField.getSubfield(S)));
    }
  }

  private void handleField(String tag, Resource instance, DataField dataField, org.marc4j.marc.Record marcRecord) {
    fieldPreprocessor.apply(new PreprocessorContext(marcRecord, dataField))
      .ifPresent(field ->
        rules.findBibFieldRules(tag)
          .stream()
          .filter(rule -> conditionChecker
            .isMarc2LdConditionSatisfied(rule.getOriginal(), dataField, marcRecord.getControlFields()))
          .forEach(fr -> fieldController.handleField(instance, field, marcRecord.getControlFields(), fr))
      );
  }

  private String readSubfieldValue(Subfield subfield) {
    if (isNull(subfield) || isNull(subfield.getData())) {
      return null;
    }
    return subfield.getData().strip();
  }

  private InstanceAndWork createInstanceAndWork() {
    var work = new Resource().addType(WORK);
    var instance = new Resource().addType(INSTANCE);
    instance.setInstanceMetadata(new InstanceMetadata().setSource(MARC));
    instance.addOutgoingEdge(new ResourceEdge(instance, work, INSTANTIATES));
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
