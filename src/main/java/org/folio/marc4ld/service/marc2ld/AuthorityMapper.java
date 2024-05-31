package org.folio.marc4ld.service.marc2ld;

import java.util.Collection;
import org.folio.ld.dictionary.model.Resource;

public interface AuthorityMapper {

  Collection<Resource> fromAuthorityMarcJson(String marc);

}
