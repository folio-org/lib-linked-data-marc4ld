package org.folio.marc4ld.service.marc2ld.preprocessor.impl;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataField043Preprocessor implements DataFieldPreprocessor {

  private static final char CODE_A = 'a';

  private final DictionaryProcessor dictionaryProcessor;
  private final MarcFactory marcFactory;

  @Override
  public Optional<DataField> preprocess(DataField dataField) {
    var result = marcFactory.newDataField(dataField.getTag(), dataField.getIndicator1(), dataField.getIndicator2());
    dataField.getSubfields()
      .forEach(sf -> {
        if (sf.getCode() == CODE_A) {
          result.addSubfield(marcFactory.newSubfield(CODE_A, sf.getData().replaceAll("-+$", EMPTY)));
        }
        result.addSubfield(sf);
      });
    return Optional.of(result)
      .filter(this::isValid);
  }

  @Override
  public String getTag() {
    return "043";
  }

  public boolean isValid(DataField dataField) {
    return dictionaryProcessor.getValue("NAME", dataField.getSubfield(CODE_A).getData()).isPresent();
  }
}
