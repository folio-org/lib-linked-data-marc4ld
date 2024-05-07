package org.folio.marc4ld.service.ld2marc.field;

import com.fasterxml.jackson.databind.JsonNode;

public interface IndicatorRuleApplier {

  char map(JsonNode node);
}
