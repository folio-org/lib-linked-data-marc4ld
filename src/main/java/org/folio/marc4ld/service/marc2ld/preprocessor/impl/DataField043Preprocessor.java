package org.folio.marc4ld.service.marc2ld.preprocessor.impl;

import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataField043Preprocessor implements DataFieldPreprocessor {

  private final DictionaryProcessor dictionaryProcessor;

  @Override
  public void preprocess(DataField dataField) {
    dataField.getSubfields('a')
      .forEach(subfield -> subfield.setData(subfield.getData().replaceAll("-+$", "")));
  }

  @Override
  public String getTag() {
    return "043";
  }

  @Override
  public boolean isValid(DataField dataField) {
    return dictionaryProcessor.getValue("NAME", dataField.getSubfield('a').getData()).isPresent();
  }
}
