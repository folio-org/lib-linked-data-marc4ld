package org.folio.marc4ld.service.ld2marc.mapper;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.marc4j.marc.DataField;

/**
 * Represents an additional mapping step applied after the initial config-based Linked Data to MARC mapping.
 * These mappers are executed after the base configuration-based mappings are applied.
 * Use cases include:
 *     - Enriching the mapped data with additional marc fields
 *     - Overriding default mappings for certain tags or conditions
 *     - Handling complex mapping rules not representable in configuration files
 */
public interface AdditionalDataFieldsMapper extends Predicate<ResourceEdge>,
  BiFunction<ResourceEdge, DataField, DataField> {

  default DataField map(ResourceEdge resourceEdge, DataField mappedSoFar) {
    if (this.test(resourceEdge)) {
      return this.apply(resourceEdge, mappedSoFar);
    }
    return mappedSoFar;
  }
}
