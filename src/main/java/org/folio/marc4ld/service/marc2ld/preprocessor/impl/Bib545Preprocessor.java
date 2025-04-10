package org.folio.marc4ld.service.marc2ld.preprocessor.impl;

import static org.folio.marc4ld.util.Constants.ONE;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.TAG_545;
import static org.folio.marc4ld.util.Constants.ZERO;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Bib545Preprocessor implements DataFieldPreprocessor {

  private static final List<String> TAGS = List.of(TAG_545);

  private final MarcFactory marcFactory;

  @Override
  public Optional<DataField> preprocess(PreprocessorContext context) {
    var originalDataField = context.dataField();
    var preprocessedDataField = duplicateDataField(originalDataField, marcFactory);
    editIndicator1(preprocessedDataField);
    return Optional.of(preprocessedDataField);
  }

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  private void editIndicator1(DataField dataField) {
    var indicator1 = dataField.getIndicator1();
    if (indicator1 != ZERO && indicator1 != ONE) {
      dataField.setIndicator1(SPACE);
    }
  }
}
