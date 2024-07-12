package org.folio.marc4ld.service.marc2ld.preprocessor.impl;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.Dictionary.GEOGRAPHIC_CODE_TO_NAME;
import static org.folio.marc4ld.util.Constants.TAG_043;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Bib043Preprocessor implements DataFieldPreprocessor {

  private static final List<String> TAGS = List.of(TAG_043);

  private final DictionaryProcessor dictionaryProcessor;
  private final MarcFactory marcFactory;

  @Override
  public Optional<DataField> preprocess(PreprocessorContext context) {
    var dataField = context.dataField();
    var result = marcFactory.newDataField(dataField.getTag(), dataField.getIndicator1(), dataField.getIndicator2());
    dataField.getSubfields()
      .forEach(sf -> {
        if (sf.getCode() == A) {
          result.addSubfield(marcFactory.newSubfield(A, sf.getData().replaceAll("-+$", EMPTY)));
        } else {
          result.addSubfield(sf);
        }
      });
    return Optional.of(result)
      .filter(this::isValid);
  }

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  public boolean isValid(DataField dataField) {
    return Optional.ofNullable(dataField.getSubfield(A))
      .map(Subfield::getData)
      .flatMap(data -> dictionaryProcessor.getValue(GEOGRAPHIC_CODE_TO_NAME, data))
      .isPresent();
  }
}
