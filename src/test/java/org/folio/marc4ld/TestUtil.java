package org.folio.marc4ld;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import lombok.experimental.UtilityClass;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.impl.DataFieldImpl;
import org.marc4j.marc.impl.SubfieldImpl;

@UtilityClass
public class TestUtil {

  public static Subfield createSubfield(char subfieldCode, String subfieldData) {
    return new SubfieldImpl(subfieldCode, subfieldData);
  }

  public static DataField createDataField(String tag, List<Subfield> subfields) {
    var dataField = new DataFieldImpl(tag, ' ', ' ');
    subfields.forEach(dataField::addSubfield);
    return dataField;
  }

  public static void validateSubfield(DataField dataField, char subfield, String expectedValue) {
    assertEquals(expectedValue, dataField.getSubfield(subfield).getData());
  }
}
