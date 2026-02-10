package org.folio.marc4ld.service.ld2marc.field;

import java.util.Collection;
import org.folio.marc4ld.service.ld2marc.field.param.SubFieldParameter;
import tools.jackson.databind.JsonNode;

public interface SubFieldRuleApplier {

  Collection<SubFieldParameter> map(JsonNode node);
}
