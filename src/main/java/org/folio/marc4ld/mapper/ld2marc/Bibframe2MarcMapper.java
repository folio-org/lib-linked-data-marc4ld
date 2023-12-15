package org.folio.marc4ld.mapper.ld2marc;

import org.folio.marc4ld.model.Resource;

public interface Bibframe2MarcMapper {

  String toMarcJson(Resource bibframe);

}
