package org.folio.marc4ld.mapper.test;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;
import org.folio.marc4ld.service.marc2ld.authority.identifier.IdentifierUrlProvider;

public class TestIdentifierUrlProvider implements IdentifierUrlProvider {
  private static final List<String> LC_PREFIXES = List.of(
    "n", "no", "nb", "nr", "ns", "sh", "dg", "sj", "gf", "mp"
  );

  private static final String LC_LINK_PREFIX = "http://id.loc.gov/authorities/";

  @Override
  public Optional<String> getBaseUrl(String identifierPrefix) {
    return ofNullable(isLcAuthority(identifierPrefix) ? LC_LINK_PREFIX : null);
  }

  private boolean isLcAuthority(String identifierPrefix) {
    return LC_PREFIXES.contains(identifierPrefix);
  }
}
