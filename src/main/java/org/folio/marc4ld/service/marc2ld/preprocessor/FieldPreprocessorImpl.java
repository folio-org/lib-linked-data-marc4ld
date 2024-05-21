package org.folio.marc4ld.service.marc2ld.preprocessor;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Component;

@Component
public class FieldPreprocessorImpl implements FieldPreprocessor {

  private final Map<String, DataFieldPreprocessor> dataFieldPreprocessors;
  private final DataFieldPreprocessor defaultProcessor;

  public FieldPreprocessorImpl(Collection<DataFieldPreprocessor> fieldPreprocessors) {
    this.dataFieldPreprocessors = fieldPreprocessors.stream()
      .collect(Collectors.toMap(DataFieldPreprocessor::getTag, Function.identity()));
    defaultProcessor = new DefaultProcessor();
  }

  @Override
  public Optional<DataField> apply(DataField dataField) {
    return dataFieldPreprocessors.getOrDefault(dataField.getTag(), defaultProcessor)
      .preprocess(dataField);
  }

  private static final class DefaultProcessor implements DataFieldPreprocessor {
    @Override
    public Optional<DataField> preprocess(DataField dataField) {
      return Optional.of(dataField);
    }

    @Override
    public String getTag() {
      return StringUtils.EMPTY;
    }
  }
}
