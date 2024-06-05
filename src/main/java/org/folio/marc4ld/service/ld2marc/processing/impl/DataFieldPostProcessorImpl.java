package org.folio.marc4ld.service.ld2marc.processing.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc4ld.service.ld2marc.processing.DataFieldPostProcessor;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombiner;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombiner.Context;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombinerFactory;
import org.marc4j.marc.DataField;

@RequiredArgsConstructor
public class DataFieldPostProcessorImpl implements DataFieldPostProcessor {

  private final DataFieldCombinerFactory combinerFactory;

  @Override
  public Collection<DataField> apply(Collection<DataField> dataFields, Set<ResourceTypeDictionary> resourceTypes) {
    var combiners = new ConcurrentHashMap<Context, DataFieldCombiner>();

    for (var dataField : dataFields) {
      var tag = dataField.getTag();
      var context = new Context(tag, resourceTypes);
      var combiner = getCombiner(context, combiners);
      combiner.add(dataField);
    }

    return combiners.values()
      .stream()
      .map(DataFieldCombiner::build)
      .flatMap(Collection::stream)
      .toList();
  }

  private DataFieldCombiner getCombiner(Context context, Map<Context, DataFieldCombiner> combiners) {
    return Optional.ofNullable(combiners.get(context))
      .orElseGet(() -> {
        var combiner = combinerFactory.create(context);
        combiners.put(context, combiner);
        return combiner;
      });
  }
}
