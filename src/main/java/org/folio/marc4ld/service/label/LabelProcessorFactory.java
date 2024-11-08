package org.folio.marc4ld.service.label;

import java.util.Collection;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.service.label.processor.LabelProcessor;

public interface LabelProcessorFactory {

  Collection<LabelProcessor> get(Marc4LdRules.LabelRule rule);
}
