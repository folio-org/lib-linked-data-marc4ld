package org.folio.marc4ld.service.ld2marc.leader.enums;

public enum ControlType {
  NO_SPECIFIED_TYPE(' '),
  ARCHIVAL('a');

  public final char value;

  ControlType(char value) {
    this.value = value;
  }
}
