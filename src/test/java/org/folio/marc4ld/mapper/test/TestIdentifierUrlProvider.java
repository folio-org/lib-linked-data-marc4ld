package org.folio.marc4ld.mapper.test;

import java.util.Optional;
import java.util.stream.Stream;
import org.folio.marc4ld.service.marc2ld.authority.identifier.IdentifierLinkProvider;

public class TestIdentifierUrlProvider implements IdentifierLinkProvider {
  @Override
  public Optional<String> getIdentifierLink(String identifier) {
    String link = null;
    if (isFastIdentifier(identifier)) {
      link = "http://id.worldcat.org/fast/" + identifier;
    } else if (isGenreFormLcIdentifier(identifier)) {
      link = "http://id.loc.gov/authorities/genreForms/" + identifier;
    } else if (isLcIdentifier(identifier)) {
      link = "http://id.loc.gov/authorities/" + identifier;
    }
    return Optional.ofNullable(link);
  }

  private boolean isFastIdentifier(String identifier) {
    return identifier.toLowerCase().startsWith("fst");
  }

  private boolean isGenreFormLcIdentifier(String identifier) {
    return identifier.toLowerCase().startsWith("gf");
  }

  private boolean isLcIdentifier(String identifier) {
    return Stream.of("n", "no", "nb", "nr", "ns", "sh", "dg", "sj", "mp")
      .anyMatch(identifier.toLowerCase()::startsWith);
  }
}
