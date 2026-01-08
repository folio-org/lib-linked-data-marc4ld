package org.folio.marc4ld.service.marc2ld.authority.identifier;

import java.util.Optional;

public interface IdentifierUrlProvider {
  Optional<String> getBaseUrl(String identifierPrefix);
}
