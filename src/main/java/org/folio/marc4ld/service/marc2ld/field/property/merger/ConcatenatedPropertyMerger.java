package org.folio.marc4ld.service.marc2ld.field.property.merger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.marc4ld.service.marc2ld.field.property.Property;

@RequiredArgsConstructor
public class ConcatenatedPropertyMerger implements PropertyMerger {

  private final String concatWith;

  @Override
  public void merge(Map<String, List<String>> values, Property property) {
    var key = PropertyDictionary.valueOf(property.field()).getValue();
    values.merge(key, new ArrayList<>(List.of(property.value())), this::concatenate);
  }

  private List<String> concatenate(List<String> values, List<String> newValues) {
    var concatenated = values.getFirst().concat(concatWith).concat(newValues.getFirst());
    values.set(0, concatenated);
    return values;
  }
}
