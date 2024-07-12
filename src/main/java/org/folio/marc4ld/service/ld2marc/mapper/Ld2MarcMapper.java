package org.folio.marc4ld.service.ld2marc.mapper;

import org.folio.ld.dictionary.model.ResourceEdge;
import org.marc4j.marc.DataField;

public interface Ld2MarcMapper {

  boolean canMap(ResourceEdge resourceEdge);

  DataField map(ResourceEdge resourceEdge);
}
