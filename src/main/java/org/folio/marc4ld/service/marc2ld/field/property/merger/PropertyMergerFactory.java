package org.folio.marc4ld.service.marc2ld.field.property.merger;

import org.folio.marc4ld.configuration.property.Marc4BibframeRules;

public interface PropertyMergerFactory {

  PropertyMerger get(Marc4BibframeRules.FieldRule rule);

  PropertyMerger getConstant(Marc4BibframeRules.FieldRule rule);
}
