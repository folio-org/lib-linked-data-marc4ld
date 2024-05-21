package org.folio.marc4ld.service.marc2ld.field.property.merger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.marc4ld.service.marc2ld.field.property.Property;
import org.folio.marc4ld.service.marc2ld.field.property.merger.function.MergeFunction;

@RequiredArgsConstructor
public class PropertyMergerImpl implements PropertyMerger {

  private final MergeFunction defaultMergeFunction;
  private final Map<String, MergeFunction> functions;

  @Override
  public void merge(Map<String, List<String>> values, Property property) {
    var key = PropertyDictionary.valueOf(property.field()).getValue();
    var function = functions.getOrDefault(property.field(), defaultMergeFunction);
    values.merge(key, new ArrayList<>(List.of(property.value())), function);
  }
}
