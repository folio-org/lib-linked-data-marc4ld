package org.folio.marc4ld.service.ld2marc.processing.combine.impl;

import java.util.Collection;
import java.util.Set;

public class TitleCombiner extends AbstractDataFieldCombiner {

  private static final Collection<Character> NON_REPEATABLE_FIELDS = Set.of('a', 'b', 'c');
  private static final Collection<Character> REPEATABLE_FIELDS = Set.of('n', 'p');

  @Override
  protected Collection<Character> getNonRepeatableFields() {
    return NON_REPEATABLE_FIELDS;
  }

  @Override
  protected Collection<Character> getRepeatableFields() {
    return REPEATABLE_FIELDS;
  }
}
