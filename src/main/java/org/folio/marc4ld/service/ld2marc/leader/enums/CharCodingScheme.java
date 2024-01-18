package org.folio.marc4ld.service.ld2marc.leader.enums;

public enum CharCodingScheme {
  MARC_8(' '),
  UCS_OR_UNICODE('a');

  public final char value;

  CharCodingScheme(char value) {
    this.value = value;
  }
}
