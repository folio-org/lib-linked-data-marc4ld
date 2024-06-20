package org.folio.marc4ld.util;

import lombok.experimental.UtilityClass;
import org.marc4j.marc.DataField;

@UtilityClass
public class MarcUtil {

  public static boolean isSubfieldPresent(char subfield, DataField dataField) {
    return dataField.getSubfield(subfield) != null;
  }

  public static String getSubfieldValue(char subfield, DataField dataField) {
    return dataField.getSubfield(subfield).getData();
  }
}
