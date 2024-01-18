package org.folio.marc4ld.service.ld2marc.leader.enums;

public enum BibliographLevel {
  MONOGRAPHIC_COMPONENT_PART('a'),
  SERIAL_COMPONENT_PART('b'),
  COLLECTION('c'),
  SUBUNIT('d'),
  INTEGRATING_RESOURCE('i'),
  MONOGRAPH_OR_ITEM('m'),
  SERIAL('s');

  public final char value;

  BibliographLevel(char value) {
    this.value = value;
  }
}
