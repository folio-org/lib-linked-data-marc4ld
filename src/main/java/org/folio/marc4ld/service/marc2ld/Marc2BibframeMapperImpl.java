package org.folio.marc4ld.service.marc2ld;

import static java.lang.Character.MIN_VALUE;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.util.BibframeUtil.getFirst;
import static org.folio.marc4ld.util.BibframeUtil.isNotEmpty;
import static org.folio.marc4ld.util.Constants.DependencyInjection.DATA_FIELD_PREPROCESSORS_MAP;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;
import static org.folio.marc4ld.util.Constants.SUBFIELD_SRS_ID;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.service.marc2ld.field.FieldMapper;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor;
import org.marc4j.MarcJsonReader;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class Marc2BibframeMapperImpl implements Marc2BibframeMapper {
  private final Marc4BibframeRules rules;
  private final FieldMapper fieldMapper;
  private final Map<String, DataFieldPreprocessor> dataFieldPreprocessorsMap;
  private final MarcFactory marcFactory;
  private final FingerprintHashService hashService;

  public Marc2BibframeMapperImpl(Marc4BibframeRules rules, FingerprintHashService hashService, FieldMapper fieldMapper,
                                 @Qualifier(DATA_FIELD_PREPROCESSORS_MAP)
                                 Map<String, DataFieldPreprocessor> dataFieldPreprocessorsMap,
                                 MarcFactory marcFactory) {
    this.rules = rules;
    this.hashService = hashService;
    this.fieldMapper = fieldMapper;
    this.dataFieldPreprocessorsMap = dataFieldPreprocessorsMap;
    this.marcFactory = marcFactory;
  }

  @Override
  public Resource fromMarcJson(String marc) {
    if (isEmpty(marc)) {
      log.warn("Given marc is empty [{}]", marc);
      return null;
    }
    var reader = getReader(marc);
    var instance = new Resource().addType(INSTANCE);
    while (reader.hasNext()) {
      fillInstanceFields(reader.next(), instance);
    }
    instance.setLabel(selectInstanceLabel(instance));
    setWorkLabel(instance);
    cleanEmptyEdges(instance);
    instance.setId(hashService.hash(instance));
    return instance;
  }

  private void setWorkLabel(Resource instance) {
    instance.getOutgoingEdges()
      .stream()
      .filter(re -> INSTANTIATES.equals(re.getPredicate()))
      .findFirst()
      .ifPresent(re -> re.getTarget().setLabel(instance.getLabel()));
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
    var localDataField = new AtomicReference<>(dataField);
    ofNullable(rules.getFieldRules().get(tag)).ifPresent(frs -> {
        var preprocessedOk = ofNullable(dataFieldPreprocessorsMap.get(dataField.getTag()))
          .map(preprocessor -> {
            localDataField.set(preprocessor.preprocess(dataField));
            return preprocessor.isValid(localDataField.get());
          })
          .orElse(true);
        if (Boolean.TRUE.equals(preprocessedOk)) {
          frs.forEach(fr -> fieldMapper.handleField(instance, localDataField.get(), marcRecord.getControlFields(), fr));
        }
      }
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

  private String selectInstanceLabel(Resource instance) {
    var labels = instance.getOutgoingEdges().stream()
      .filter(e -> Objects.equals(TITLE.getUri(), e.getPredicate().getUri()))
      .map(re -> re.getTarget().getLabel())
      .toList();
    return getFirst(labels);
  }

  private void cleanEmptyEdges(Resource resource) {
    resource.setOutgoingEdges(resource.getOutgoingEdges().stream()
      .map(re -> {
        cleanEmptyEdges(re.getTarget());
        return re;
      })
      .filter(re -> isNotEmpty(re.getTarget()))
      .collect(Collectors.toCollection(LinkedHashSet::new))
    );
  }

  private MarcJsonReader getReader(String marc) {
    return new MarcJsonReader(new ByteArrayInputStream(marc.getBytes(StandardCharsets.UTF_8)));
  }
}
