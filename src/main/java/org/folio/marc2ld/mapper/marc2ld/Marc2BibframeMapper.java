package org.folio.marc2ld.mapper.marc2ld;

import org.folio.marc2ld.model.Resource;

public interface Marc2BibframeMapper {

  Resource map(String marc);

}
