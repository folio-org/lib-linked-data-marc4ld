package org.folio.marc4ld.service.marc2ld.preprocessor;

import org.marc4j.marc.DataField;

public interface DataFieldPreprocessor {

  void preprocess(DataField dataField);

  String getTag();
}
