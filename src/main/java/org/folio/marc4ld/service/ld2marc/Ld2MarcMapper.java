package org.folio.marc4ld.service.ld2marc;

import org.folio.ld.dictionary.model.Resource;

public interface Ld2MarcMapper {

  String toMarcJson(Resource resource);

}
