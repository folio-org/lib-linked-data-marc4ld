package org.folio.marc4ld.service.marc2ld.authority.identifier;

import static java.util.Optional.ofNullable;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCSH;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCDGT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCGFT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCMPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCNAF;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCSH;

import java.util.Optional;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.springframework.stereotype.Service;

@Service
class IdentifierLinkServiceImpl implements IdentifierLinkService {
  private static final Set<ResourceTypeDictionary> LC_TYPES = Set.of(
    ID_LCCSH, ID_LCDGT, ID_LCGFT, ID_LCMPT, ID_LCSH, ID_LCNAF
  );

  private static final String LC_LINK_PREFIX = "http://id.loc.gov/authorities/";

  @Override
  public Optional<String> getIdentifierLink(String identifier, ResourceTypeDictionary identifierType) {
    return ofNullable(isLcAuthority(identifierType) ? LC_LINK_PREFIX + identifier : null);
  }

  private boolean isLcAuthority(ResourceTypeDictionary identifierType) {
    return LC_TYPES.contains(identifierType);
  }
}
