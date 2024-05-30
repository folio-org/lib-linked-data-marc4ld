package org.folio.marc4ld.service.label;

import java.util.Collection;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.service.label.processor.LabelProcessor;

public interface LabelProcessorFactory {

  Collection<LabelProcessor> get(Marc4BibframeRules.LabelRule rule);
}
