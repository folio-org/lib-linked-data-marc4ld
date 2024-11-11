package org.folio.marc4ld.service.marc2ld.field.property.merger;

import org.folio.marc4ld.configuration.property.Marc4LdRules;

public interface PropertyMergerFactory {

  PropertyMerger get(Marc4LdRules.FieldRule rule);

  PropertyMerger getConstant(Marc4LdRules.FieldRule rule);
}
