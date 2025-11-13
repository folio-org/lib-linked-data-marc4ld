package org.folio.marc4ld.service.marc2ld.mapper;

import java.util.List;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.dto.MarcData;

/**
 * Represents an additional mapping step applied after the initial config-based MARC to Linked Data mapping.
 * These mappers are executed after the base configuration-based mappings are applied.
 * Use cases include:
 *     - Enriching the mapped data with additional triples
 *     - Overriding default mappings for certain tags or conditions
 *     - Handling complex mapping rules not representable in configuration files
 */
public interface AdditionalMapper {

  List<String> getTags();

  boolean canMap(Marc4LdRules.FieldRule fieldRule);

  void map(MarcData marcData, Resource mappedSofar);
}
