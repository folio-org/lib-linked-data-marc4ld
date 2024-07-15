package org.folio.marc4ld.service.ld2marc.mapper;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.marc4j.marc.DataField;

public interface Ld2MarcMapper extends Predicate<ResourceEdge>, Function<ResourceEdge, DataField> {

  default Optional<DataField> map(ResourceEdge resourceEdge) {
    return Optional.of(resourceEdge)
      .filter(this)
      .map(this);
  }
}
