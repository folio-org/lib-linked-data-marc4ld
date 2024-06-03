package org.folio.marc4ld.service.marc2ld;

import static java.lang.Character.MIN_VALUE;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.marc4ld.util.BibframeUtil.isNotEmpty;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;
import static org.folio.marc4ld.util.Constants.SUBFIELD_SRS_ID;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.condition.ConditionChecker;
import org.folio.marc4ld.service.label.LabelService;
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
public class AuthorityMapperImpl implements AuthorityMapper {

  private final MarcFactory marcFactory;
  private final FieldPreprocessor fieldPreprocessor;
  private final Marc2ldRules rules;
  private final ConditionChecker conditionChecker;
  private final FieldController fieldController;
  private final FingerprintHashService hashService;
  private final LabelService labelService;

  @Override
  public Collection<Resource> fromAuthorityMarcJson(String marc) {
    if (isEmpty(marc)) {
      log.warn("Given marc is empty [{}]", marc);
      return null;
    }
    //TODO temporary
    var topLevelResources = new ArrayList<Resource>();

    var reader = getReader(marc);
    while (reader.hasNext()) {
      var resource = new Resource() //TODO temporary
        .addType(CONCEPT).addType(PERSON)
        ;
      fillInstanceFields(reader.next(), resource);

      resource.setId(hashService.hash(resource));
//      cleanEmptyEdges(resource);

      topLevelResources.add(resource);
    }


    return topLevelResources;
  }


  //TODO duplicate from original mapper
  private void fillInstanceFields(org.marc4j.marc.Record marcRecord, Resource instance) {
    marcRecord.getDataFields()
      .forEach(dataField -> fillData(marcRecord, instance, dataField));
    marcRecord.getControlFields()
      .forEach(controlField -> fillControl(marcRecord, instance, controlField));
  }


  //TODO duplicate from original mapper
  private void fillControl(org.marc4j.marc.Record marcRecord, Resource instance, ControlField controlField) {
    var dataField = marcFactory.newDataField(EMPTY, MIN_VALUE, MIN_VALUE);
    handleField(controlField.getTag(), instance, dataField, marcRecord);
  }

  //TODO duplicate from original mapper
  private void fillData(org.marc4j.marc.Record marcRecord, Resource instance, DataField dataField) {
    handleField(dataField.getTag(), instance, dataField, marcRecord);

    //TODO remove inventory?
//    if (FIELD_UUID.equals(dataField.getTag())) {
//      instance.setInventoryId(readUuid(dataField.getSubfield(SUBFIELD_INVENTORY_ID)));
//      instance.setSrsId(readUuid(dataField.getSubfield(SUBFIELD_SRS_ID)));
//    }
  }

  private void handleField(String tag, Resource instance, DataField dataField, org.marc4j.marc.Record marcRecord) {
    fieldPreprocessor.apply(dataField)
      .ifPresent(field ->
        rules.findAuthorityFiledRules(tag)
          .stream()
          .filter(rule -> conditionChecker
            .isMarc2LdConditionSatisfied(rule.getOriginal(), dataField, marcRecord.getControlFields()))
          .forEach(fr -> fieldController.handleField(instance, field, marcRecord.getControlFields(), fr))
      );
  }

  //TODO duplicate from original mapper
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

  //TODO duplicate from original mapper
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


  //TODO duplicate from original mapper
  private MarcJsonReader getReader(String marc) {
    return new MarcJsonReader(new ByteArrayInputStream(marc.getBytes(StandardCharsets.UTF_8)));
  }
}
