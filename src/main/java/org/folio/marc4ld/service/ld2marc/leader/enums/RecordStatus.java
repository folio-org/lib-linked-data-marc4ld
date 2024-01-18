package org.folio.marc4ld.service.ld2marc.leader.enums;

public enum RecordStatus {
  INCREASE_IN_ENCODING_LEVEL('a'),
  CORRECTED_OR_REVISED('c'),
  DELETED('d'),
  NEW('n'),
  INCREASE_IN_ENCODING_FROM_PREPUBLICATION('p');

  public final char value;

  RecordStatus(char value) {
    this.value = value;
  }
}
