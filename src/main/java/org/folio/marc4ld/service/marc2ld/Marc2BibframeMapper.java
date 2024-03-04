package org.folio.marc4ld.service.marc2ld;

import org.folio.ld.dictionary.model.Resource;

public interface Marc2BibframeMapper {

  Resource fromMarcJson(String marc);

}
