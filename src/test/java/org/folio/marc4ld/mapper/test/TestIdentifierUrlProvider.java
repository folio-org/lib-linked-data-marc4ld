package org.folio.marc4ld.mapper.test;

import java.util.Optional;
import java.util.stream.Stream;
import org.folio.marc4ld.service.marc2ld.authority.identifier.IdentifierLinkProvider;

public class TestIdentifierUrlProvider implements IdentifierLinkProvider {
  @Override
  public Optional<String> getIdentifierLink(String identifier) {
    if (isLcIdentifier(identifier)) {
      return Optional.of("http://id.loc.gov/authorities/" + identifier);
    }
    if (isFastIdentifier(identifier)) {
      return Optional.of("http://id.worldcat.org/fast/" + identifier);
    }
    return Optional.empty();
  }

  private boolean isFastIdentifier(String identifier) {
    return identifier.toLowerCase().startsWith("fst");
  }

  private boolean isLcIdentifier(String identifier) {
    return Stream.of("n", "no", "nb", "nr", "ns", "sh", "dg", "sj", "gf", "mp")
      .anyMatch(identifier.toLowerCase()::startsWith);
  }
}
