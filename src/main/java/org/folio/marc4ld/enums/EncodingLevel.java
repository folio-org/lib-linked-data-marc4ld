package org.folio.marc4ld.enums;

public enum EncodingLevel {
  FULL_LEVEL(' '),
  FULL_LEVEL_MATERIAL_NOT_EXAMINED('1'),
  LESS_THAN_FULL_LEVEL_MATERIAL_NOT_EXAMINED('2'),
  ABBREVIATED_LEVEL('3'),
  CORE_LEVEL('4'),
  PARTIAL_PRELIMINARY_LEVEL('5'),
  MINIMAL_LEVEL('7'),
  PREPUBLICATION_LEVEL('8'),
  UNKNOWN('u'),
  NOT_APPLICABLE('z');

  public final char value;

  EncodingLevel(char value) {
    this.value = value;
  }
}
