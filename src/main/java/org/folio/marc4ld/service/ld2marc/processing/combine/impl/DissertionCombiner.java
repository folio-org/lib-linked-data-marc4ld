package org.folio.marc4ld.service.ld2marc.processing.combine.impl;


import java.util.Collection;
import java.util.Set;

public class DissertionCombiner extends AbstractDataFieldCombiner {
  private static final Collection<Character> NON_REPEATABLE_FIELDS = Set.of('a', 'b', 'c', 'd');
  private static final Collection<Character> REPEATABLE_FIELDS = Set.of('g', 'o');

  @Override
  protected Collection<Character> getNonRepeatableFields() {
    return NON_REPEATABLE_FIELDS;
  }

  @Override
  protected Collection<Character> getRepeatableFields() {
    return REPEATABLE_FIELDS;
  }
}
