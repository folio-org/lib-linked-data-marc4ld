package org.folio.marc4ld.service.ld2marc.processing;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.marc4j.marc.DataField;

public interface DataFieldPostProcessor
  extends BiFunction<Collection<DataField>, Set<ResourceTypeDictionary>, Collection<DataField>> {
}
