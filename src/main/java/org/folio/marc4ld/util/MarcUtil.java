package org.folio.marc4ld.util;

import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.UnaryOperator;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.folio.marc4ld.enums.BibliographLevel;
import org.folio.marc4ld.enums.RecordType;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

@UtilityClass
public class MarcUtil {

  public static boolean isSubfieldPresent(char subfield, DataField dataField) {
    return dataField.getSubfield(subfield) != null;
  }

  public static String getSubfieldValue(char subfield, DataField dataField) {
    return dataField.getSubfield(subfield).getData();
  }

  public static Optional<String> getSubfieldValueWithoutSpaces(DataField dataField, char code) {
    return getSubfieldApplyingTransformation(dataField, code, StringUtils::deleteWhitespace);
  }

  public static Optional<String> getSubfieldValueStripped(DataField dataField, char code) {
    return getSubfieldApplyingTransformation(dataField, code, String::strip);
  }

  private static Optional<String> getSubfieldApplyingTransformation(DataField dataField, char code,
                                                                    UnaryOperator<String> function) {
    return Optional.ofNullable(dataField.getSubfield(code))
      .filter(subfield -> nonNull(subfield.getData()))
      .map(subfield -> function.apply(subfield.getData()));
  }

  public static void orderSubfields(DataField dataField, Comparator<Subfield> comparator) {
    var subfields = new ArrayList<>(dataField.getSubfields());
    subfields.forEach(dataField::removeSubfield);
    subfields.sort(comparator);
    subfields.forEach(dataField::addSubfield);
  }

  public static boolean isLanguageMaterial(char typeOfRecord) {
    return typeOfRecord == RecordType.LANGUAGE_MATERIAL.value;
  }

  public static boolean isMonographicComponentPartOrItem(char bibliographicLevel) {
    return bibliographicLevel == BibliographLevel.MONOGRAPHIC_COMPONENT_PART.value
      || bibliographicLevel == BibliographLevel.MONOGRAPH_OR_ITEM.value;
  }

  public static void sortFields(Record marcRecord) {
    if (isEmpty(marcRecord.getVariableFields())) {
      return;
    }
    var variableFields = new ArrayList<>(marcRecord.getVariableFields());
    variableFields.forEach(marcRecord::removeVariableField);
    variableFields.stream()
      .sorted(comparing(VariableField::getTag))
      .forEach(marcRecord::addVariableField);
  }
}
