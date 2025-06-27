package org.folio.marc4ld.service.ld2marc.mapper;

import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.resource.field.ControlFieldsBuilder;

/**
 * Defines a contract for custom Linked Data to MARC mappers for control fields that implement complex or
 * non-configurable mapping logic.
 * These mappers operate independently of the config-based mappings.
 */
public interface CustomControlFieldsMapper {

  void map(Resource resource, ControlFieldsBuilder controlFieldsBuilder);
}
