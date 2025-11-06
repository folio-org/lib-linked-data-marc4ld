package org.folio.marc4ld.service.marc2ld.condition;

import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;

public interface Marc2LdConditionChecker {
  boolean isMarc2LdConditionSatisfied(Marc4LdRules.FieldRule fieldRule, DataField dataField, Record marcRecord);
}
