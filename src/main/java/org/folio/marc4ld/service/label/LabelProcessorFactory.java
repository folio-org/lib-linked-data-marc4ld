package org.folio.marc4ld.service.label;

import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.service.label.processor.LabelProcessor;

public interface LabelProcessorFactory {

  LabelProcessor get(Marc4BibframeRules.LabelRule rule);
}
