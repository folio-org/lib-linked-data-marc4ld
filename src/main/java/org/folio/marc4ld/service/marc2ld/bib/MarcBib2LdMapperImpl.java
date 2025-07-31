package org.folio.marc4ld.service.marc2ld.bib;

import static java.lang.Character.MIN_VALUE;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.ld.dictionary.model.ResourceSource.MARC;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.S;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;
import static org.folio.marc4ld.util.LdUtil.getFirst;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.FolioMetadata;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.condition.ConditionChecker;
import org.folio.marc4ld.service.marc2ld.Marc2ldRules;
import org.folio.marc4ld.service.marc2ld.field.ResourceProcessor;
import org.folio.marc4ld.service.marc2ld.mapper.CustomMapper;
import org.folio.marc4ld.service.marc2ld.normalization.MarcBibPunctuationNormalizerImpl;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor.PreprocessorContext;
import org.folio.marc4ld.service.marc2ld.preprocessor.FieldPreprocessor;
import org.folio.marc4ld.service.marc2ld.reader.MarcReaderProcessor;
import org.folio.marc4ld.service.marc2ld.relation.EmptyEdgesCleaner;
import org.folio.marc4ld.util.TypeUtil;
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
  private final List<CustomMapper> customMappers;
  private final MarcBibPunctuationNormalizerImpl marcPunctuationNormalizer;

  @Override
  public Optional<Resource> fromMarcJson(String marc) {
    if (isEmpty(marc)) {
      log.warn("Given marc is empty [{}]", marc);
      return empty();
    }
    var records = marcReaderProcessor.readMarc(marc).toList();
    if (records.size() > 1) {
      log.warn("Given marc contains [{}] record(s), but only the first is to be mapped", records.size());
    }
    return createInstanceAndWorkResource(records.getFirst());
  }

  private Optional<Resource> createInstanceAndWorkResource(Record marcRecord) {
    return getWorkType(marcRecord)
      .map(this::createInstanceAndWork)
      .map(iaw -> {
        fillInstanceFields(marcRecord, iaw.instance);
        emptyEdgesCleaner.apply(iaw.instance);
        setAdditionInfo(iaw);
        return iaw.instance;
      });
  }

  private Optional<ResourceTypeDictionary> getWorkType(Record marcRecord) {
    var workType = TypeUtil.getWorkType(marcRecord.getLeader());
    if (workType.isEmpty()) {
      log.warn("Given marc record is not of supported types ({}), skipping: [{}]",
        TypeUtil.getSupportedRecordTypes(), marcRecord);
    }
    return workType;
  }

  private void fillInstanceFields(org.marc4j.marc.Record marcRecord, Resource instance) {
    marcPunctuationNormalizer.normalize(marcRecord);
    marcRecord.getDataFields()
      .forEach(dataField -> fillData(marcRecord, instance, dataField));
    marcRecord.getControlFields()
      .forEach(controlField -> fillControl(marcRecord, instance, controlField));
    customMappers.stream()
      .filter(customMapper -> customMapper.isApplicable(marcRecord))
      .forEach(customMapper -> customMapper.map(marcRecord, instance));
  }

  private void fillControl(org.marc4j.marc.Record marcRecord, Resource instance, ControlField controlField) {
    var dataField = marcFactory.newDataField(EMPTY, MIN_VALUE, MIN_VALUE);
    handleField(controlField.getTag(), instance, dataField, marcRecord);
  }

  private void fillData(org.marc4j.marc.Record marcRecord, Resource instance, DataField dataField) {
    handleField(dataField.getTag(), instance, dataField, marcRecord);
    if (FIELD_UUID.equals(dataField.getTag())) {
      instance.getFolioMetadata()
        .setInventoryId(readSubfieldValue(dataField.getSubfield(SUBFIELD_INVENTORY_ID)))
        .setSrsId(readSubfieldValue(dataField.getSubfield(S)));
    }
  }

  private void handleField(String tag, Resource instance, DataField dataField, org.marc4j.marc.Record marcRecord) {
    fieldPreprocessor.apply(new PreprocessorContext(marcRecord, dataField))
      .forEach(field ->
        rules.findBibFieldRules(tag)
          .stream()
          .filter(rule -> conditionChecker
            .isMarc2LdConditionSatisfied(rule.getOriginal(), field, marcRecord))
          .forEach(fr -> fieldController.handleField(instance, field, marcRecord, fr))
      );
  }

  private String readSubfieldValue(Subfield subfield) {
    if (isNull(subfield) || isNull(subfield.getData())) {
      return null;
    }
    return subfield.getData().strip();
  }

  private InstanceAndWork createInstanceAndWork(ResourceTypeDictionary workType) {
    var work = new Resource().addType(WORK).addType(workType);
    var instance = new Resource().addType(INSTANCE);
    instance.setFolioMetadata(new FolioMetadata().setSource(MARC));
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
