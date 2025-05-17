package org.folio.marc4ld.service.marc2ld.preprocessor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Component;

@Component
public class FieldPreprocessorImpl implements FieldPreprocessor {

  private final Map<List<String>, DataFieldPreprocessor> dataFieldPreprocessors;
  private final DataFieldPreprocessor defaultProcessor;

  public FieldPreprocessorImpl(Collection<DataFieldPreprocessor> fieldPreprocessors) {
    this.dataFieldPreprocessors = fieldPreprocessors.stream()
      .collect(Collectors.toMap(DataFieldPreprocessor::getTags, Function.identity()));
    defaultProcessor = new DefaultProcessor();
  }

  @Override
  public List<DataField> apply(DataFieldPreprocessor.PreprocessorContext context) {
    return dataFieldPreprocessors.entrySet()
      .stream()
      .filter(entry ->  entry.getKey().contains(context.dataField().getTag()))
      .map(Map.Entry::getValue)
      .findFirst()
      .orElse(defaultProcessor)
      .preprocess(context);
  }

  private static final class DefaultProcessor implements DataFieldPreprocessor {

    public static final List<String> TAGS = List.of(StringUtils.EMPTY);

    @Override
    public List<DataField> preprocess(PreprocessorContext context) {
      return List.of(context.dataField());
    }

    @Override
    public List<String> getTags() {
      return TAGS;
    }
  }
}
