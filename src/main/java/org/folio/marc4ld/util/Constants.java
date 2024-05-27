package org.folio.marc4ld.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  public static final String FIELD_UUID = "999";
  public static final char SUBFIELD_INVENTORY_ID = 'i';
  public static final char SUBFIELD_SRS_ID = 's';
  public static final char SPACE = ' ';
  public static final char ZERO = '0';
  public static final char ONE = '1';
  public static final char TWO = '2';
  public static final char FOUR = '4';
  public static final char A = 'a';
  public static final char B = 'b';
  public static final char Q = 'q';

  @UtilityClass
  public static class DependencyInjection {

    public static final String DICTIONARY_MAP = "dictionaryMap";
  }

  @UtilityClass
  public static class Classification {

    public static final String TAG_050 = "050";
    public static final String TAG_082 = "082";
    public static final String DLC = "http://id.loc.gov/vocabulary/organizations/dlc";
    public static final String UBA = "http://id.loc.gov/vocabulary/mstatus/uba";
    public static final String NUBA = "http://id.loc.gov/vocabulary/mstatus/nuba";
    public static final String FULL = "Full";
    public static final String ABRIDGED = "Abridged";
  }
}
