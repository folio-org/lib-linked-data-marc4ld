package org.folio.marc4ld.service.marc2ld.relation;

import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RelationImpl implements Relation {

  private final Character code;

  private final Character text;

  @Override
  public Optional<Character> getCode() {
    return Optional.ofNullable(code);
  }

  @Override
  public Optional<Character> getText() {
    return Optional.ofNullable(text);
  }
}
