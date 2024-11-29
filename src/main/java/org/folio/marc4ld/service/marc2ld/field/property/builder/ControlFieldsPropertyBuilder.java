package org.folio.marc4ld.service.marc2ld.field.property.builder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.field.property.Property;
import org.marc4j.marc.ControlField;

@RequiredArgsConstructor
public class ControlFieldsPropertyBuilder implements PropertyBuilder<Collection<ControlField>> {

  private final String tag;
  private final Map<String, List<Integer>> rules;
  private final DictionaryProcessor dictionaryProcessor;

  @Override
  public Collection<Property> apply(Collection<ControlField> controlFields) {
    return controlFields.stream()
      .filter(cf -> Objects.equals(tag, cf.getTag()))
      .map(ControlField::getData)
      .map(this::getProperties)
      .flatMap(Collection::stream)
      .toList();
  }

  private Collection<Property> getProperties(String data) {
    return rules.entrySet()
      .stream()
      .map(entry -> getValue(entry, data))
      .filter(Optional::isPresent)
      .flatMap(Optional::stream)
      .toList();
  }

  private Optional<Property> getValue(Map.Entry<String, List<Integer>> propertyField, String data) {
    var range = propertyField.getValue();
    return Optional.of(data)
      .map(cfValue -> {
        if (range.get(1) > cfValue.length()) {
          return cfValue.substring(range.get(0));
        }
        return cfValue.substring(range.get(0), range.get(1));
      })
      .map(String::strip)
      .filter(StringUtils::isNotBlank)
      .map(value -> normalize(value, propertyField.getKey()))
      .map(value -> new Property(propertyField.getKey(), value));
  }

  private String normalize(String value, String property) {
    return dictionaryProcessor.getValue(property, value)
      .orElse(value);
  }
}
