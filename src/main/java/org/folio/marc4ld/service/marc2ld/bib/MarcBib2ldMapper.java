package org.folio.marc4ld.service.marc2ld.bib;

import org.folio.ld.dictionary.model.Resource;

public interface MarcBib2ldMapper {

  Resource fromMarcJson(String marc);

}
