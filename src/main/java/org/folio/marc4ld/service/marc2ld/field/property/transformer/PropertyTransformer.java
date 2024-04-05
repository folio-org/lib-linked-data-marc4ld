package org.folio.marc4ld.service.marc2ld.field.property.transformer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@FunctionalInterface
public interface PropertyTransformer
  extends Function<Map<String, List<String>>, Collection<Map<String, List<String>>>> {
}
