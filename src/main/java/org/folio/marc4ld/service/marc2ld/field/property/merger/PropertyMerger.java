package org.folio.marc4ld.service.marc2ld.field.property.merger;

import java.util.List;
import java.util.Map;
import org.folio.marc4ld.service.marc2ld.field.property.Property;

public interface PropertyMerger {
  void merge(Map<String, List<String>> values, Property property);
}
