package org.folio.marc2ld.mapper.ld2marc;

import org.folio.marc2ld.model.Resource;

public interface Bibframe2MarcMapper {

  String map(Resource bibframe);

}
