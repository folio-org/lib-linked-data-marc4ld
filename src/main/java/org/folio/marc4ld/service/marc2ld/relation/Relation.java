package org.folio.marc4ld.service.marc2ld.relation;

import java.util.Optional;

public interface Relation {
  Optional<Character> getCode();

  Optional<Character> getText();
}
