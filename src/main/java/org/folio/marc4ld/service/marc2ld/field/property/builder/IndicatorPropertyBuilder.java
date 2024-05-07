package org.folio.marc4ld.service.marc2ld.field.property.builder;


import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.service.marc2ld.field.property.Property;
import org.marc4j.marc.DataField;

@RequiredArgsConstructor
public class IndicatorPropertyBuilder implements PropertyBuilder<DataField> {

  private final Marc4BibframeRules.FieldRule rule;

  @Override
  public Collection<Property> apply(DataField dataField) {
    return Stream.concat(
        getIndicator1(dataField).stream(),
        getIndicator2(dataField).stream()
      )
      .toList();
  }

  private Optional<Property> getIndicator1(DataField dataField) {
    if (Objects.isNull(rule.getInd1())) {
      return Optional.empty();
    }
    return Optional.of(dataField)
      .map(DataField::getIndicator1)
      .filter(this::isNotEmptyIndicator)
      .map(String::valueOf)
      .map(ind -> new Property(rule.getInd1(), ind));
  }

  private Optional<Property> getIndicator2(DataField dataField) {
    if (Objects.isNull(rule.getInd2())) {
      return Optional.empty();
    }
    return Optional.of(dataField)
      .map(DataField::getIndicator2)
      .filter(this::isNotEmptyIndicator)
      .map(String::valueOf)
      .map(ind -> new Property(rule.getInd2(), ind));
  }

  private boolean isNotEmptyIndicator(char indicator) {
    return !Character.isSpaceChar(indicator) && indicator != Character.MIN_VALUE;
  }
}
