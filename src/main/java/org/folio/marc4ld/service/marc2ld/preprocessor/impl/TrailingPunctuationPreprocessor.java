package org.folio.marc4ld.service.marc2ld.preprocessor.impl;

import static org.folio.marc4ld.util.Constants.TAG_260;
import static org.folio.marc4ld.util.Constants.TAG_262;
import static org.folio.marc4ld.util.Constants.TAG_264;
import static org.folio.marc4ld.util.Constants.TAG_300;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrailingPunctuationPreprocessor implements DataFieldPreprocessor {
  private static final String TRAILING_PUNCTUATION_REGEX = "[.,:;\\s]+\\s*$";

  private static final List<String> TAGS = List.of(TAG_260, TAG_262, TAG_264, TAG_300);

  private final MarcFactory marcFactory;

  @Override
  public Optional<DataField> preprocess(PreprocessorContext context) {
    var originalDataField = context.dataField();
    var preprocessedDataField = duplicateDataField(originalDataField, marcFactory);
    removeTrailingPunctuation(preprocessedDataField);
    return Optional.of(preprocessedDataField);
  }

  private void removeTrailingPunctuation(DataField dataField) {
    dataField.getSubfields()
      .forEach(subfield -> {
        var processedData = clean(subfield.getData());
        subfield.setData(processedData);
      });
  }

  private String clean(String data) {
    return data.replaceAll(TRAILING_PUNCTUATION_REGEX, "");
  }

  @Override
  public List<String> getTags() {
    return TAGS;
  }
}
