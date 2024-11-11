package org.folio.marc4ld.service.marc2ld.field.property.transformer;

import org.folio.marc4ld.configuration.property.Marc4LdRules;

public interface PropertyTransformerFactory {

  PropertyTransformer get(Marc4LdRules.FieldRule rule);
}
