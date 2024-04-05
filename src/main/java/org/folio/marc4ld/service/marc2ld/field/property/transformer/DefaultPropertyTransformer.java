package org.folio.marc4ld.service.marc2ld.field.property.transformer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DefaultPropertyTransformer implements PropertyTransformer {

  @Override
  public Collection<Map<String, List<String>>> apply(Map<String, List<String>> properties) {
    return Collections.singleton(properties);
  }
}
