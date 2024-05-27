package org.folio.marc4ld.service.label;

import org.folio.marc4ld.configuration.label.LabelRules;
import org.folio.marc4ld.service.label.processor.LabelProcessor;

public interface LabelProcessorFactory {

  LabelProcessor get(LabelRules.LabelRule rule);
}
