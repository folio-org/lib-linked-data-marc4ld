package org.folio.marc4ld.util;

import java.util.ArrayList;
import java.util.Comparator;
import lombok.experimental.UtilityClass;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

@UtilityClass
public class MarcUtil {

  public static boolean isSubfieldPresent(char subfield, DataField dataField) {
    return dataField.getSubfield(subfield) != null;
  }

  public static String getSubfieldValue(char subfield, DataField dataField) {
    return dataField.getSubfield(subfield).getData();
  }

  public static void orderSubfields(DataField dataField) {
    var subfields = new ArrayList<>(dataField.getSubfields());
    subfields.forEach(dataField::removeSubfield);
    subfields.sort(Comparator.comparingInt(Subfield::getCode));
    subfields.forEach(dataField::addSubfield);
  }
}
