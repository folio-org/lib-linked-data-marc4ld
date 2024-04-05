package org.folio.marc4ld.service.ld2marc.field;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
import org.folio.marc4ld.service.ld2marc.field.param.ControlFieldParameter;

public interface ControlFieldRule {

  Collection<ControlFieldParameter> map(JsonNode node);
}
