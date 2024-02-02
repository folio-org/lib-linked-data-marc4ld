package org.folio.marc4ld.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  public static final String FIELD_UUID = "999";
  public static final char SUBFIELD_INVENTORY_ID = 'i';
  public static final char SUBFIELD_SRS_ID = 's';

  @UtilityClass
  public static class DependencyInjection {

    public static final String DATA_FIELD_PREPROCESSORS_MAP = "dataFieldPreprocessorsMap";
  }
}
