package org.folio.marc4ld.service.label;

import java.util.Collection;
import java.util.Collections;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.service.label.processor.LabelProcessor;
import org.folio.marc4ld.service.label.processor.PropertyLabelProcessor;
import org.folio.marc4ld.service.label.processor.TemplateLabelProcessor;
import org.springframework.stereotype.Component;

@Component
public class LabelProcessorFactoryImpl implements LabelProcessorFactory {

  public static final String PATTERN_MARKER = "$";

  @Override
  public Collection<LabelProcessor> get(Marc4LdRules.LabelRule rule) {
    if (CollectionUtils.isEmpty(rule.getProperties())) {
      return Collections.emptyList();
    }
    return rule.getProperties()
      .stream()
      .map(this::getProcessor)
      .toList();
  }

  private LabelProcessor getProcessor(String property) {
    if (property.contains(PATTERN_MARKER)) {
      return new TemplateLabelProcessor(property);
    }
    return new PropertyLabelProcessor(property);
  }
}
