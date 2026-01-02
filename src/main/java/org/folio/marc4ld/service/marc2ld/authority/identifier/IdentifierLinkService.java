package org.folio.marc4ld.service.marc2ld.authority.identifier;

import java.util.Optional;
import org.folio.ld.dictionary.ResourceTypeDictionary;

interface IdentifierLinkService {
  Optional<String> getIdentifierLink(String identifier, ResourceTypeDictionary identifierType);
}
