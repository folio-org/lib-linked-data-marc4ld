package org.folio.marc4ld.service.ld2marc.field;

import java.util.Collection;
import org.folio.marc4ld.service.ld2marc.field.param.ControlFieldParameter;
import tools.jackson.databind.JsonNode;

public interface ControlFieldRuleApplier {

  Collection<ControlFieldParameter> map(JsonNode node);
}
