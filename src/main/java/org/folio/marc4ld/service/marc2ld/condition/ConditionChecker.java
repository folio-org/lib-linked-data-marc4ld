package org.folio.marc4ld.service.marc2ld.condition;

import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.marc4j.marc.DataField;

public interface ConditionChecker {

  boolean isConditionSatisfied(Marc4BibframeRules.FieldRule fieldRule, DataField dataField);

}
