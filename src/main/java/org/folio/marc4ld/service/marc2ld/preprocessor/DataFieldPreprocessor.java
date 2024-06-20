package org.folio.marc4ld.service.marc2ld.preprocessor;

import java.util.List;
import java.util.Optional;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;

public interface DataFieldPreprocessor {

  Optional<DataField> preprocess(PreprocessorContext context);

  List<String> getTags();

  record PreprocessorContext(Record marcRecord, DataField dataField){
  }
}
