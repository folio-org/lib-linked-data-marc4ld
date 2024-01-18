package org.folio.marc4ld.service.ld2marc.leader.enums;

public enum MultipartResourceRecordLevel {
  NOT_SPECIFIED_OR_NOT_APPLICABLE(' '),
  SET('a'),
  PART_WITH_INDEPENDENT_TITLE('b'),
  PART_WITH_DEPENDENT_TITLE('c');

  public final char value;

  MultipartResourceRecordLevel(char value) {
    this.value = value;
  }
}
