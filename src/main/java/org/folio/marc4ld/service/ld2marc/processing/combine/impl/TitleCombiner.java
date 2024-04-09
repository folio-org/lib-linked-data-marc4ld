package org.folio.marc4ld.service.ld2marc.processing.combine.impl;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.ObjectUtils.notEqual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombiner;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

public class TitleCombiner implements DataFieldCombiner {

  private static final Collection<Character> NON_REPEATABLE_FIELDS = Set.of('a', 'b', 'c');
  private static final Collection<Character> REPEATABLE_FIELDS = Set.of('n', 'p');
  private static final char EMPTY = ' ';

  private DataField combinedField;

  @Override
  public Collection<DataField> build() {
    if (isNull(combinedField)) {
      return Collections.emptyList();
    }
    orderFields();
    return List.of(combinedField);
  }

  @Override
  public void add(DataField dataField) {
    if (isNull(combinedField)) {
      this.combinedField = dataField;
      return;
    }
    putNonRepeatableFields(dataField);
    putRepeatableFields(dataField);
    setIndicators(dataField);
  }

  private void putNonRepeatableFields(DataField dataField) {
    NON_REPEATABLE_FIELDS
      .stream()
      .filter(this::notContainsInCombined)
      .forEach(fieldTag -> putFieldIfPresent(fieldTag, dataField));
  }

  private boolean notContainsInCombined(char fieldTag) {
    return Optional.ofNullable(combinedField.getSubfield(fieldTag))
      .map(Subfield::getData)
      .filter(StringUtils::isNotBlank)
      .isEmpty();
  }

  private void putRepeatableFields(DataField dataField) {
    REPEATABLE_FIELDS
      .forEach(fieldTag -> putFieldIfPresent(fieldTag, dataField));
  }

  private void putFieldIfPresent(char fieldTag, DataField dataField) {
    Optional.ofNullable(dataField.getSubfield(fieldTag))
      .ifPresent(newField -> combinedField.addSubfield(newField));
  }

  private void setIndicators(DataField dataField) {
    if (Objects.equals(combinedField.getIndicator1(), EMPTY) && notEqual(dataField.getIndicator1(), EMPTY)) {
      combinedField.setIndicator1(dataField.getIndicator1());
    }
    if (Objects.equals(combinedField.getIndicator2(), EMPTY) && notEqual(dataField.getIndicator2(), EMPTY)) {
      combinedField.setIndicator2(dataField.getIndicator2());
    }
  }

  private void orderFields() {
    var newFields = new ArrayList<>(combinedField.getSubfields());
    newFields
      .forEach(combinedField::removeSubfield);
    newFields
      .sort(Comparator.comparingInt(Subfield::getCode));
    newFields
      .forEach(combinedField::addSubfield);
  }
}