package org.folio.marc4ld.service.condition;

import java.util.List;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.model.Resource;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

public interface ConditionChecker {

  boolean isMarc2LdConditionSatisfied(Marc4BibframeRules.FieldRule fieldRule, DataField dataField,
                                      List<ControlField> controlFields);

  boolean isLd2MarcConditionSatisfied(Marc4BibframeRules.FieldRule fieldRule, Resource resource);

}
