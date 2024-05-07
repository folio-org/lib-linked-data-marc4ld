package org.folio.marc4ld.service.marc2ld.field.property.transformer;

import org.folio.marc4ld.configuration.property.Marc4BibframeRules;

public interface PropertyTransformerFactory {

  PropertyTransformer get(Marc4BibframeRules.FieldRule rule);
}
