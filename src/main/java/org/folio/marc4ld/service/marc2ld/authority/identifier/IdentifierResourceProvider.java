package org.folio.marc4ld.service.marc2ld.authority.identifier;

import java.util.Set;
import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.Record;

public interface IdentifierResourceProvider {
  Set<Resource> getIdentifierResources(Record marc);
}
