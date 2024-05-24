package org.folio.marc4ld.service.marc2ld.label;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.springframework.stereotype.Component;

@Component
public class LabelProcessorFactoryImpl implements LabelProcessorFactory {

  private final LabelProcessor defaultLabelProcessor = new UuidLabelProcessor();

  @Override
  public LabelProcessor get(Marc4BibframeRules.FieldRule rule) {
    return Optional.of(rule)
      .map(Marc4BibframeRules.FieldRule::getLabel)
      .filter(StringUtils::isNotBlank)
      .map(this::createProcessor)
      .orElse(defaultLabelProcessor);
  }

  private LabelProcessor createProcessor(String label) {
    if (label.contains("${")) {
      return new TemplateLabelProcessor(label);
    }
    var property = PropertyDictionary.valueOf(label).getValue();
    return new SinglePropertyLabelProcessor(property, defaultLabelProcessor);
  }
}
