package org.folio.marc4ld.service.ld2marc.processing.combine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.marc4j.marc.DataField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataFieldCombinerFactoryImpl implements DataFieldCombinerFactory {

  private final Map<String, List<CombinerFactory>> combinerMap;

  @Autowired
  public DataFieldCombinerFactoryImpl(Collection<CombinerFactory> combinerFactories) {
    this.combinerMap = combinerFactories.stream()
      .collect(Collectors.groupingBy(CombinerFactory::getTag));
  }

  @Override
  public DataFieldCombiner create(DataFieldCombiner.Context context) {
    return combinerMap.getOrDefault(context.marcTag(), Collections.emptyList())
      .stream()
      .filter(factory -> factory.test(context))
      .map(CombinerFactory::get)
      .findFirst()
      .orElseGet(DefaultDataFieldCombiner::new);
  }

  private static final class DefaultDataFieldCombiner implements DataFieldCombiner {
    private final List<DataField> fields = new ArrayList<>();

    @Override
    public void add(DataField dataField) {
      fields.add(dataField);
    }

    @Override
    public Collection<DataField> build() {
      return fields;
    }
  }
}
