package org.folio.marc4ld.service.marc2ld.authority;

import java.util.Collection;
import org.folio.ld.dictionary.model.Resource;

public interface MarcAuthority2ldMapper {

  Collection<Resource> fromMarcJson(String marc);

}
