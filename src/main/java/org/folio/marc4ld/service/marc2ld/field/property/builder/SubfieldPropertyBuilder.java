package org.folio.marc4ld.service.marc2ld.field.property.builder;


import java.util.Collection;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.folio.marc4ld.service.marc2ld.field.property.Property;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

@RequiredArgsConstructor
public class SubfieldPropertyBuilder implements PropertyBuilder<DataField> {

  @Getter
  private final char marcSubfield;
  private final String graphProperty;

  @Override
  public Collection<Property> apply(DataField dataField) {
    return dataField.getSubfields(marcSubfield)
      .stream()
      .map(Subfield::getData)
      .filter(Objects::nonNull)
      .map(String::trim)
      .filter(StringUtils::isNotEmpty)
      .map(v -> new Property(graphProperty, v))
      .toList();
  }
}
