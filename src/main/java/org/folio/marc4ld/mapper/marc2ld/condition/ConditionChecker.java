package org.folio.marc4ld.mapper.marc2ld.condition;

import org.folio.marc4ld.configuration.property.Marc2BibframeRules;
import org.marc4j.marc.DataField;

public interface ConditionChecker {

  boolean isConditionSatisfied(Marc2BibframeRules.FieldRule fieldRule, DataField dataField);

}
