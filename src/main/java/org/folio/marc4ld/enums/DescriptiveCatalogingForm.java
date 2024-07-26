package org.folio.marc4ld.enums;

public enum DescriptiveCatalogingForm {
  NON_ISBD(' '),
  AACR_2('a'),
  ISBD_PUNCTUATION_OMITTED('c'),
  ISBD_PUNCTUATION_INCLUDED('i'),
  NON_ISBD_PUNCTUATION_OMITTED('n'),
  UNKNOWN('u');

  public final char value;

  DescriptiveCatalogingForm(char value) {
    this.value = value;
  }
}
