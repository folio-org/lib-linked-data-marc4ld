package org.folio.marc4ld.service.ld2marc.field;

import tools.jackson.databind.JsonNode;

public interface IndicatorRuleApplier {

  char map(JsonNode node);
}
