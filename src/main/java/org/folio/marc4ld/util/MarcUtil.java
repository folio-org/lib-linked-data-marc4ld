package org.folio.marc4ld.util;

import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.folio.marc4ld.util.LdUtil.getPropertyValue;
import static org.folio.marc4ld.util.LdUtil.getPropertyValues;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.UnaryOperator;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.enums.BibliographLevel;
import org.folio.marc4ld.enums.RecordType;
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

  public static boolean isBook(Leader leader) {
    var typeOfRecord = leader.getTypeOfRecord();
    var bibliographicLevel = leader.getImplDefined1()[0];
    return isLanguageMaterial(typeOfRecord) && isBookLevel(bibliographicLevel);
  }

  public static boolean isSerial(Leader leader) {
    var bibliographicLevel = leader.getImplDefined1()[0];
    return bibliographicLevel == BibliographLevel.SERIAL.value
      || bibliographicLevel == BibliographLevel.SERIAL_COMPONENT_PART.value;
  }

  private static boolean isLanguageMaterial(char typeOfRecord) {
    return typeOfRecord == RecordType.LANGUAGE_MATERIAL.value
      || typeOfRecord == RecordType.MANUSCRIPT_LANGUAGE_MATERIAL.value;
  }

  private static boolean isBookLevel(char bibliographicLevel) {
    return isMonographicComponentPartOrItem(bibliographicLevel)
      || isCollection(bibliographicLevel)
      || isSubUnit(bibliographicLevel);
  }

  private static boolean isMonographicComponentPartOrItem(char bibliographicLevel) {
    return bibliographicLevel == BibliographLevel.MONOGRAPHIC_COMPONENT_PART.value
      || bibliographicLevel == BibliographLevel.MONOGRAPH_OR_ITEM.value;
  }

  private static boolean isCollection(char bibliographicLevel) {
    return bibliographicLevel == BibliographLevel.COLLECTION.value;
  }

  private static boolean isSubUnit(char bibliographicLevel) {
    return bibliographicLevel == BibliographLevel.SUBUNIT.value;
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
