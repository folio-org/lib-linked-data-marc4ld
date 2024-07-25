package org.folio.marc4ld.service.marc2ld.bib;

import java.util.Optional;
import org.folio.ld.dictionary.model.Resource;

public interface MarcBib2ldMapper {

  Optional<Resource> fromMarcJson(String marc);

}
