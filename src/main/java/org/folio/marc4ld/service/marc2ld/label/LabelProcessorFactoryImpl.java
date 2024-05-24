package org.folio.marc4ld.service.marc2ld.label;

import java.util.Optional;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.springframework.stereotype.Component;

@Component
public class LabelProcessorFactoryImpl implements LabelProcessorFactory {

  private final LabelProcessor defaultLabelProcessor = new UuidLabelProcessor();

  @Override
  public LabelProcessor get(Marc4BibframeRules.FieldRule rule) {
    return getLabel(rule)
      .map(this::createSinglePropertyProcessor)
      .orElse(defaultLabelProcessor);
  }

  private Optional<String> getLabel(Marc4BibframeRules.FieldRule rule) {
    return Optional.of(rule)
      .map(Marc4BibframeRules.FieldRule::getLabel)
      .map(PropertyDictionary::valueOf)
      .map(PropertyDictionary::getValue);
  }

  private LabelProcessor createSinglePropertyProcessor(String label) {
    return new SinglePropertyLabelProcessor(label, defaultLabelProcessor);
  }
}
