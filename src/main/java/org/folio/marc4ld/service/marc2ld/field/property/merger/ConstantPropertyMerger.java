package org.folio.marc4ld.service.marc2ld.field.property.merger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.marc4ld.service.marc2ld.field.property.Property;
import org.springframework.stereotype.Component;

@Component
public class ConstantPropertyMerger implements PropertyMerger {

  private static final String PERCENT = "%";

  @Override
  public void merge(Map<String, List<String>> properties, Property property) {
    if (isSplittable(property)) {
      putDividedValue(properties, property);
    } else {
      properties.put(getField(property), List.of(property.value()));
    }
  }

  private boolean isSplittable(Property property) {
    return property.value()
      .contains(PERCENT);
  }

  private void putDividedValue(Map<String, List<String>> properties, Property property) {
    var value = property.value();
    var propertyEnumName = value.substring(value.indexOf(PERCENT) + 1);
    var propertyName = PropertyDictionary.valueOf(propertyEnumName)
      .getValue();
    Optional.of(properties)
      .map(p -> p.get(propertyName))
      .map(propValues -> mapValues(propValues, value))
      .ifPresent(values -> properties.put(getField(property), new ArrayList<>(values)));
  }

  private LinkedHashSet<String> mapValues(List<String> propValues, String value) {
    return propValues
      .stream()
      .map(prop -> value.substring(0, value.indexOf(PERCENT)) + prop)
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private String getField(Property property) {
    return PropertyDictionary.valueOf(property.field())
      .getValue();
  }
}
