package org.folio.marc4ld.service.marc2ld.field.property.transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SeparatePropertyTransformer implements PropertyTransformer {

  @Override
  public Collection<Map<String, List<String>>> apply(Map<String, List<String>> properties) {
    List<Map<String, List<String>>> transformedList = new ArrayList<>();
    if (properties == null || properties.isEmpty()) {
      return transformedList;
    }
    int listSize = -1;
    for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
      if (listSize == -1) {
        listSize = entry.getValue().size();
      } else if (entry.getValue().size() != listSize) {
        throw new IllegalArgumentException("All lists must have the same length");
      }
    }
    for (int i = 0; i < listSize; i++) {
      transformedList.add(new HashMap<>());
    }
    for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
      List<String> values = entry.getValue();
      for (int i = 0; i < listSize; i++) {
        transformedList.get(i).computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(values.get(i));
      }
    }
    return transformedList;
  }
}
