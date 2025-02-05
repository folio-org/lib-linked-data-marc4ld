package org.folio.marc4ld.service.ld2marc;

import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.enums.UnmappedMarcHandling;

public interface Ld2MarcMapper {

  String toMarcJson(Resource resource);

  String toMarcJson(Resource resource, UnmappedMarcHandling marcHandling);

}
