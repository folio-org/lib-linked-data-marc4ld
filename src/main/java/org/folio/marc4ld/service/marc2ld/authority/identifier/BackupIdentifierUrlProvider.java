package org.folio.marc4ld.service.marc2ld.authority.identifier;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(name = "identifierUrlProvider")
public class BackupIdentifierUrlProvider implements IdentifierUrlProvider {
  private static final List<String> LC_PREFIXES = List.of(
    "n", "no", "nb", "nr", "ns", "sh", "dg", "sj", "gf", "mp"
  );

  private static final String LC_LINK_PREFIX = "http://id.loc.gov/authorities/";

  @Override
  public Optional<String> getBaseUrl(String identifierPrefix) {
    return ofNullable(isLcAuthority(identifierPrefix) ? LC_LINK_PREFIX : null);
  }

  private boolean isLcAuthority(String identifierPrefix) {
    String lower = identifierPrefix.toLowerCase();
    return LC_PREFIXES.stream().anyMatch(lower::startsWith);
  }
}
