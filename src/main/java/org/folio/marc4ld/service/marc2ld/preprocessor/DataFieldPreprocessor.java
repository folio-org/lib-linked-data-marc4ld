package org.folio.marc4ld.service.marc2ld.preprocessor;

import java.util.List;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

public interface DataFieldPreprocessor {

  List<DataField> preprocess(PreprocessorContext context);

  List<String> getTags();

  default DataField duplicateDataField(DataField originalDataField, MarcFactory marcFactory) {
    var dataField = marcFactory.newDataField(originalDataField.getTag(), originalDataField.getIndicator1(),
      originalDataField.getIndicator2());
    originalDataField.getSubfields()
      .forEach(dataField::addSubfield);
    return dataField;
  }

  record PreprocessorContext(Record marcRecord, DataField dataField){
  }
}
