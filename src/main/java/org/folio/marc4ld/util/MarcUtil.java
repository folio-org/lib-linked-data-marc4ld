package org.folio.marc4ld.util;

import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.folio.marc4ld.util.LdUtil.getPropertyValue;
import static org.folio.marc4ld.util.LdUtil.getPropertyValues;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

@UtilityClass
public class MarcUtil {

  public static DataField duplicateDataField(DataField originalDataField, MarcFactory marcFactory) {
    var newDataField = marcFactory.newDataField(
      originalDataField.getTag(),
      originalDataField.getIndicator1(),
      originalDataField.getIndicator2()
    );
    originalDataField.getSubfields()
      .forEach(newDataField::addSubfield);
    return newDataField;
  }

  public static void addRepeatableSubfield(Resource resource, String property, DataField dataField,
                                           char subfield, MarcFactory marcFactory) {
    getPropertyValues(resource, property)
      .stream()
      .map(val -> marcFactory.newSubfield(subfield, val))
      .forEach(dataField::addSubfield);
  }

  public static void addNonRepeatableSubfield(Resource resource, String property, DataField dataField, char subfield,
                                              MarcFactory marcFactory) {
    getPropertyValue(resource, property)
      .ifPresent(val -> dataField.addSubfield(marcFactory.newSubfield(subfield, val)));
  }

  public static boolean isSubfieldPresent(char subfield, DataField dataField) {
    return dataField.getSubfield(subfield) != null;
  }

  public static boolean hasSubfield(Subfield subfield, DataField dataField) {
    return dataField.getSubfields()
      .stream()
      .anyMatch(
        sf -> Objects.equals(sf.getCode(), subfield.getCode())
          && Objects.equals(sf.getData(), subfield.getData())
      );
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

  public static char getBibliographicLevel(Leader leader) {
    return leader.getImplDefined1()[0];
  }
}
