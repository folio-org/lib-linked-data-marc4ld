package org.folio.marc4ld.service.marc2ld.preprocessor;

import java.util.Optional;
import org.marc4j.marc.DataField;

public interface DataFieldPreprocessor {

  Optional<DataField> preprocess(DataField dataField);

  String getTag();
}
