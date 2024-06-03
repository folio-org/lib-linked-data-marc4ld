package org.folio.marc4ld.service.marc2ld;

import static java.lang.Character.MIN_VALUE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.condition.ConditionChecker;
import org.folio.marc4ld.service.marc2ld.field.FieldController;
import org.folio.marc4ld.service.marc2ld.preprocessor.FieldPreprocessor;
import org.marc4j.MarcJsonReader;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
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
        .addType(CONCEPT).addType(PERSON);
      fillInstanceFields(reader.next(), resource);

      resource.setId(hashService.hash(resource));

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
  private MarcJsonReader getReader(String marc) {
    return new MarcJsonReader(new ByteArrayInputStream(marc.getBytes(StandardCharsets.UTF_8)));
  }
}
