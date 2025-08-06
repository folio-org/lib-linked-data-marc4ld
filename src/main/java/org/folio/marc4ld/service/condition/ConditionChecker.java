package org.folio.marc4ld.service.condition;

import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;

public interface ConditionChecker {

  boolean isMarc2LdConditionSatisfied(Marc4LdRules.FieldRule fieldRule, DataField dataField, Record marcRecord);

  boolean isLd2MarcConditionSatisfied(Marc4LdRules.FieldRule fieldRule, Resource resource, Resource parent);

}
