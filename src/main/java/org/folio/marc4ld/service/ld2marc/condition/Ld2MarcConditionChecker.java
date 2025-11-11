package org.folio.marc4ld.service.ld2marc.condition;

import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;

public interface Ld2MarcConditionChecker {
  boolean isLd2MarcConditionSatisfied(Marc4LdRules.FieldRule fieldRule, Resource resource, Resource parent);
}
