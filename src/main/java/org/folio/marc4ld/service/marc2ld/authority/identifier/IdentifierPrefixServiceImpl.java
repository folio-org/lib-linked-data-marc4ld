package org.folio.marc4ld.service.marc2ld.authority.identifier;

import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class IdentifierPrefixServiceImpl implements IdentifierPrefixService {
  private static final Pattern NON_ALPHA_PATTERN = Pattern.compile("[^a-zA-Z]");

  @Override
  public String getIdentifierPrefix(String identifier) {
    return identifier == null ? "" : NON_ALPHA_PATTERN.split(identifier, 2)[0];
  }
}
