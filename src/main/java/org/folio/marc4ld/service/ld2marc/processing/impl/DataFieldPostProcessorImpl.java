package org.folio.marc4ld.service.ld2marc.processing.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.service.ld2marc.processing.DataFieldPostProcessor;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombiner;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombinerFactory;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataFieldPostProcessorImpl implements DataFieldPostProcessor {

  private final DataFieldCombinerFactory combinerFactory;

  @Override
  public Collection<DataField> apply(Collection<DataField> dataFields) {
    var combiners = new ConcurrentHashMap<String, DataFieldCombiner>();

    for (var dataField : dataFields) {
      var tag = dataField.getTag();
      var combiner = getCombiner(tag, combiners);
      combiner.add(dataField);
    }

    return combiners.values()
      .stream()
      .map(DataFieldCombiner::build)
      .flatMap(Collection::stream)
      .toList();
  }

  private DataFieldCombiner getCombiner(String tag, Map<String, DataFieldCombiner> combiners) {
    return Optional.ofNullable(combiners.get(tag))
      .orElseGet(() -> {
        var combiner = combinerFactory.create(tag);
        combiners.put(tag, combiner);
        return combiner;
      });
  }
}
