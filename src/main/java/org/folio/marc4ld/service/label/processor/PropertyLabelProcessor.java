package org.folio.marc4ld.service.label.processor;

import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.SPACE;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PropertyDictionary;

public class PropertyLabelProcessor implements LabelProcessor {

  private final Collection<String> basicProperties;
  private final LabelProcessor defaultProcessor;

  public PropertyLabelProcessor(Collection<String> basicProperties, LabelProcessor defaultProcessor) {
    this.defaultProcessor = defaultProcessor;
    this.basicProperties = basicProperties
      .stream()
      .map(PropertyDictionary::valueOf)
      .map(PropertyDictionary::getValue)
      .toList();
  }

  @Override
  public String apply(Map<String, List<String>> properties) {
    return basicProperties.stream()
      .map(properties::get)
      .filter(Objects::nonNull)
      .findFirst()
      .map(vs -> join(SPACE, vs))
      .orElseGet(() -> defaultProcessor.apply(properties));
  }
}
