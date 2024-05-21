package org.folio.marc4ld.service.marc2ld.field.property.builder;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.field.property.Property;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

@RequiredArgsConstructor
public class MappingPropertyBuilder implements PropertyBuilder<DataField> {

  private final String property;
  private final Character field;
  private final DictionaryProcessor dictionaryProcessor;

  @Override
  public Collection<Property> apply(DataField dataField) {
    return Optional.of(dataField)
      .map(df -> df.getSubfield(field))
      .map(Subfield::getData)
      .filter(StringUtils::isNotEmpty)
      .flatMap(v -> dictionaryProcessor.getValue(property, v))
      .map(v -> new Property(property, v))
      .map(Collections::singletonList)
      .orElseGet(Collections::emptyList);
  }
}
