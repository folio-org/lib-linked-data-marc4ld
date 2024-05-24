package org.folio.marc4ld.service.marc2ld.label;

import org.folio.marc4ld.configuration.property.Marc4BibframeRules;

public interface LabelProcessorFactory {

  LabelProcessor get(Marc4BibframeRules.FieldRule rule);
}
