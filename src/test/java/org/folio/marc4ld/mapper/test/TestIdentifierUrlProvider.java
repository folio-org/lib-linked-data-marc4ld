package org.folio.marc4ld.mapper.test;

import java.util.Optional;
import org.folio.marc4ld.service.marc2ld.authority.identifier.IdentifierUrlProvider;

public class TestIdentifierUrlProvider implements IdentifierUrlProvider {
  @Override
  public Optional<String> getBaseUrl(String identifierPrefix) {
    var result = switch (identifierPrefix) {
      case "n", "no", "nb", "nr", "ns", "sh", "dg", "sj", "gf", "mp" -> "http://id.loc.gov/authorities/";
      case "fst" -> "http://id.worldcat.org/fast";
      default -> null;
    };

    return Optional.ofNullable(result);
  }
}
