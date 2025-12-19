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
  public static final char EIGHT = '8';
  public static final char NINE = '9';
  public static final char A = 'a';
  public static final char B = 'b';
  public static final char C = 'c';
  public static final char D = 'd';
  public static final char E = 'e';
  public static final char F = 'f';
  public static final char J = 'j';
  public static final char L = 'l';
  public static final char M = 'm';
  public static final char N = 'n';
  public static final char P = 'p';
  public static final char Q = 'q';
  public static final char R = 'r';
  public static final char S = 's';
  public static final char T = 't';
  public static final char V = 'v';
  public static final char X = 'x';
  public static final char Y = 'y';
  public static final char Z = 'z';
  public static final String TAG_005 = "005";
  public static final String TAG_008 = "008";
  public static final String TAG_024 = "024";
  public static final String TAG_043 = "043";
  public static final String TAG_100 = "100";
  public static final String TAG_110 = "110";
  public static final String TAG_111 = "111";
  public static final String TAG_130 = "130";
  public static final String TAG_240 = "240";
  public static final String TAG_245 = "245";
  public static final String TAG_257 = "257";
  public static final String TAG_300 = "300";
  public static final String TAG_340 = "340";
  public static final String TAG_490 = "490";
  public static final String TAG_600 = "600";
  public static final String TAG_630 = "630";
  public static final String TAG_545 = "545";
  public static final String TAG_700 = "700";
  public static final String TAG_710 = "710";
  public static final String TAG_711 = "711";
  public static final String TAG_730 = "730";
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
    public static final String AGENT_TEXT_TO_PREDICATE = "AGENT_TEXT_TO_PREDICATE";
    public static final String CHARACTERISTIC_CODE_TO_LINK = "CHARACTERISTIC_CODE_TO_LINK";
    public static final String CHARACTERISTIC_CODE_TO_TERM = "CHARACTERISTIC_CODE_TO_TERM";
    public static final String PUBLICATION_FREQUENCY_CODE_TO_LABEL = "PUBLICATION_FREQUENCY_CODE_TO_LABEL";
  }
}
