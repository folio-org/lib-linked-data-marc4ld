package org.folio.marc4ld.service.marc2ld.preprocessor.impl;

import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.B;
import static org.folio.marc4ld.util.Constants.S;
import static org.folio.marc4ld.util.Constants.T;
import static org.folio.marc4ld.util.Constants.TAG_245;
import static org.folio.marc4ld.util.Constants.TAG_775;
import static org.folio.marc4ld.util.Constants.TAG_776;
import static org.folio.marc4ld.util.MarcUtil.getSubfieldValue;
import static org.folio.marc4ld.util.MarcUtil.isSubfieldPresent;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Bib7xxPreprocessor implements DataFieldPreprocessor {

  private static final List<String> TAGS = List.of(TAG_775, TAG_776);

  private final MarcFactory marcFactory;

  @Override
  public List<DataField> preprocess(PreprocessorContext context) {
    var originalDataField = context.dataField();
    var preprocessedDataField = duplicateDataField(originalDataField, marcFactory);
    addSubfieldsRelatedToTitle(context.marcRecord(), preprocessedDataField);
    return List.of(preprocessedDataField);
  }

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  private void addSubfieldsRelatedToTitle(Record marcRecord, DataField dataField) {
    setSubfieldT(marcRecord, dataField);
    setSubfieldS(dataField, getSubfieldValue(T, dataField));
  }

  private void setSubfieldT(Record marcRecord, DataField dataField) {
    if (isSubfieldPresent(T, dataField)) {
      return;
    }
    var titleFrom245 = getTitle(marcRecord);
    dataField.addSubfield(marcFactory.newSubfield(T, titleFrom245));
  }

  private void setSubfieldS(DataField dataField, String subfieldValueT) {
    if (isSubfieldPresent(S, dataField)) {
      return;
    }
    dataField.addSubfield(marcFactory.newSubfield(S, subfieldValueT));
  }

  private String getTitle(Record marcRecord) {
    return marcRecord.getDataFields()
      .stream()
      .filter(dataField -> TAG_245.equals(dataField.getTag()))
      .findFirst()
      .map(dataField -> isSubfieldPresent(B, dataField)
        ? getSubfieldValue(A, dataField) + " " + getSubfieldValue(B, dataField)
        : getSubfieldValue(A, dataField))
      .orElseThrow();
  }
}
