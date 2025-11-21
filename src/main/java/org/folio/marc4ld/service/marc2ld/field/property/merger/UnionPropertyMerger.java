package org.folio.marc4ld.service.marc2ld.field.property.merger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.ListUtils;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.marc4ld.service.marc2ld.field.property.Property;
import org.springframework.stereotype.Component;

@Component
public class UnionPropertyMerger implements PropertyMerger {
  @Override
  public void merge(Map<String, List<String>> values, Property property) {
    var key = PropertyDictionary.valueOf(property.field()).getValue();
    values.merge(key, new ArrayList<>(List.of(property.value())), ListUtils::union);
  }
}
