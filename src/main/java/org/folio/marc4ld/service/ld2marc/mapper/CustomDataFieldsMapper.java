package org.folio.marc4ld.service.ld2marc.mapper;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.marc4j.marc.DataField;

/**
 * Specifies an interface for custom mappers that handle complex or non-configurable Linked Data to MARC data field
 * conversions. These mappers function independent of configuration-driven mapping logic.
 */
public interface CustomDataFieldsMapper extends Predicate<ResourceEdge>, Function<ResourceEdge, DataField> {

  default Optional<DataField> map(ResourceEdge resourceEdge) {
    return Optional.of(resourceEdge)
      .filter(this)
      .map(this);
  }
}
