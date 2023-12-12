package org.folio.marc2ld.mapper.marc2ld.condition;

import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.marc4j.marc.DataField;

public interface ConditionChecker {

  boolean isConditionSatisfied(Marc2BibframeRules.FieldRule fieldRule, DataField dataField);

}
