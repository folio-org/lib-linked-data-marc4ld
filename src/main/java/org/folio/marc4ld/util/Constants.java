package org.folio.marc4ld.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  public static final String FIELD_UUID = "999";
  public static final char INDICATOR_FOLIO = 'f';
  public static final char SUBFIELD_INVENTORY_ID = 'i';
  public static final char SPACE = ' ';
  public static final char ZERO = '0';
  public static final char ONE = '1';
  public static final char TWO = '2';
  public static final char THREE = '3';
  public static final char FOUR = '4';
  public static final char SEVEN = '7';
  public static final char NINE = '9';
  public static final char A = 'a';
  public static final char B = 'b';
  public static final char E = 'e';
  public static final char J = 'j';
  public static final char Q = 'q';
  public static final char S = 's';
  public static final char T = 't';
  public static final char Z = 'z';
  public static final String TAG_008 = "008";
  public static final String TAG_010 = "010";
  public static final String TAG_020 = "020";
  public static final String TAG_043 = "043";
  public static final String TAG_100 = "100";
  public static final String TAG_110 = "110";
  public static final String TAG_111 = "111";
  public static final String TAG_245 = "245";
  public static final String TAG_257 = "257";
  public static final String TAG_260 = "260";
  public static final String TAG_262 = "262";
  public static final String TAG_264 = "264";
  public static final String TAG_300 = "300";
  public static final String TAG_700 = "700";
  public static final String TAG_710 = "710";
  public static final String TAG_711 = "711";
  public static final String TAG_775 = "775";
  public static final String TAG_776 = "776";

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
    public static final String DDC = "ddc";
    public static final String LC = "lc";
  }

  @UtilityClass
  public static class Dictionary {

    public static final String GEOGRAPHIC_CODE_TO_NAME = "GEOGRAPHIC_CODE_TO_NAME";
    public static final String AGENT_CODE_TO_PREDICATE = "AGENT_CODE_TO_PREDICATE";
    public static final String AGENT_TEXT_TO_PREDICATE = "AGENT_TEXT_TO_PREDICATE";
  }
}
