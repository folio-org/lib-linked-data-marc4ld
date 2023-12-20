package org.folio.marc4ld.service.marc2ld;

import org.folio.marc4ld.model.Resource;

public interface Marc2BibframeMapper {

  Resource fromMarcJson(String marc);

}
