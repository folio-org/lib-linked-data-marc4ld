package org.folio.marc4ld.service.ld2marc.mapper;

import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.resource.field.ControlFieldsBuilder;

/**
 * Specifies an interface for custom mappers that handle complex or non-configurable Linked Data to MARC control field
 * conversions. These mappers function independent of configuration-driven mapping logic.
 */
public interface CustomControlFieldsMapper {

  void map(Resource resource, ControlFieldsBuilder controlFieldsBuilder);
}
