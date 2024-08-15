package org.folio.marc4ld.util;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

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

  public static String getSubfieldValueWithoutSpaces(DataField dataField, char code) {
    var subfield = dataField.getSubfield(code);
    if (isNull(subfield) || isBlank(subfield.getData())) {
      return null;
    }
    return subfield.getData().strip();
  }

  public static void orderSubfields(DataField dataField, Comparator<Subfield> comparator) {
    var subfields = new ArrayList<>(dataField.getSubfields());
    subfields.forEach(dataField::removeSubfield);
    subfields.sort(comparator);
    subfields.forEach(dataField::addSubfield);
  }
}
