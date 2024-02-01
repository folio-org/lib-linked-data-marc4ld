package org.folio.marc4ld.util;

import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  public static final String FIELD_UUID = "999";
  public static final char SUBFIELD_INVENTORY_ID = 'i';
  public static final char SUBFIELD_SRS_ID = 's';
  public static final Set<String> FIELDS_WITH_REPEATABLE_SUBFIELDS = Set.of("043");

  @UtilityClass
  public static class DependencyInjection {

    public static final String DATA_FIELD_PREPROCESSORS_MAP = "dataFieldPreprocessorsMap";
  }
}
