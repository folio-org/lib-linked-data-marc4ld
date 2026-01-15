package org.folio.marc4ld.service.marc2ld.authority.identifier;

import java.util.Optional;

public interface IdentifierLinkProvider {
  Optional<String> getIdentifierLink(String identifier);
}
