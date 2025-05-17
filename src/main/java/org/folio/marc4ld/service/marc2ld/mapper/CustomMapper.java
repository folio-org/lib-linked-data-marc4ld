package org.folio.marc4ld.service.marc2ld.mapper;

import org.folio.ld.dictionary.model.Resource;

/**
 * Defines a contract for custom MARC to Linked Data mappers that implement complex or non-configurable mapping logic.
 * These mappers operate independently of the config-based mappings.
 */
public interface CustomMapper {

  boolean isApplicable(org.marc4j.marc.Record marcRecord);

  void map(org.marc4j.marc.Record marcRecord, Resource instance);
}
