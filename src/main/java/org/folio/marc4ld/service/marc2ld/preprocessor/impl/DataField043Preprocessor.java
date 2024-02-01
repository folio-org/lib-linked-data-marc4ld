package org.folio.marc4ld.service.marc2ld.preprocessor.impl;

import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Component;

@Component
public class DataField043Preprocessor implements DataFieldPreprocessor {

  @Override
  public void preprocess(DataField dataField) {
    dataField.getSubfields('a')
      .forEach(subfield -> subfield.setData(subfield.getData().replaceAll("-+$", "")));
  }

  @Override
  public String getTag() {
    return "043";
  }
}
