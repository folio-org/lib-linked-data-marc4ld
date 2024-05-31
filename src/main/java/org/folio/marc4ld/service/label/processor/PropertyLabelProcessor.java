package org.folio.marc4ld.service.label.processor;

import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.folio.ld.dictionary.PropertyDictionary;

public class PropertyLabelProcessor implements LabelProcessor {

  public static final String PROPERTY_DELIMITER = ", ";
  private final String basicProperty;

  public PropertyLabelProcessor(String property) {
    this.basicProperty = Optional.of(property)
      .map(PropertyDictionary::valueOf)
      .map(PropertyDictionary::getValue)
      .orElse(EMPTY);
  }

  @Override
  public String apply(Map<String, List<String>> properties) {
    return Optional.of(basicProperty)
      .map(properties::get)
      .map(vs -> join(PROPERTY_DELIMITER, vs))
      .orElse(EMPTY);
  }
}
