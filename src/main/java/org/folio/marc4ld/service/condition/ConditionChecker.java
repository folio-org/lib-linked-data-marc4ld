package org.folio.marc4ld.service.condition;

import java.util.List;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

public interface ConditionChecker {

  boolean isMarc2LdConditionSatisfied(Marc4LdRules.FieldRule fieldRule, DataField dataField,
                                      List<ControlField> controlFields);

  boolean isLd2MarcConditionSatisfied(Marc4LdRules.FieldRule fieldRule, Resource resource);

}
