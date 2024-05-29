package org.folio.marc4ld.service.label;

import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.service.label.processor.LabelProcessor;
import org.folio.marc4ld.service.label.processor.PropertyLabelProcessor;
import org.folio.marc4ld.service.label.processor.TemplateLabelProcessor;
import org.folio.marc4ld.service.label.processor.UuidLabelProcessor;
import org.springframework.stereotype.Component;

@Component
public class LabelProcessorFactoryImpl implements LabelProcessorFactory {

  public static final String PATTERN_MARKER = "$";
  private final LabelProcessor defaultLabelProcessor = new UuidLabelProcessor();

  @Override
  public LabelProcessor get(Marc4BibframeRules.LabelRule rule) {
    return getSimplePropertyProcessor(rule)
      .or(() -> getPatternProcessor(rule))
      .orElse(defaultLabelProcessor);
  }

  private Optional<LabelProcessor> getSimplePropertyProcessor(Marc4BibframeRules.LabelRule rule) {
    return Optional.of(rule)
      .map(Marc4BibframeRules.LabelRule::getProperties)
      .filter(CollectionUtils::isNotEmpty)
      .map(property -> new PropertyLabelProcessor(property, defaultLabelProcessor));
  }


  private Optional<LabelProcessor> getPatternProcessor(Marc4BibframeRules.LabelRule rule) {
    return Optional.of(rule)
      .map(Marc4BibframeRules.LabelRule::getPattern)
      .filter(StringUtils::isNotBlank)
      .filter(property -> property.contains(PATTERN_MARKER))
      .map(TemplateLabelProcessor::new);
  }
}
