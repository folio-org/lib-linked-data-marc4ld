package org.folio.marc4ld.service.ld2marc.field;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
import org.folio.marc4ld.service.ld2marc.field.param.SubFieldParameter;

public interface SubFieldRule {

  Collection<SubFieldParameter> map(JsonNode node);
}
