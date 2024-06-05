package org.folio.marc4ld.service.ld2marc.processing.combine.impl;

import static org.folio.ld.dictionary.ResourceTypeDictionary.DISSERTATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombiner;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombinerFactory;
import org.marc4j.marc.DataField;

public class DataFieldCombinerFactoryImpl implements DataFieldCombinerFactory {

  private final Map<Predicate<DataFieldCombiner.Context>, DataFieldCombiner> combinerMap;

  public DataFieldCombinerFactoryImpl() {
    combinerMap = Map.of(
      c -> c.marcTag().equals("245") && c.resourceTypes().contains(INSTANCE), new TitleCombiner(),
      c -> c.marcTag().equals("010") && c.resourceTypes().contains(INSTANCE), new LccnCombiner(),
      c -> c.marcTag().equals("502") && c.resourceTypes().contains(DISSERTATION), new DissertionCombiner()
    );
  }

  @Override
  public DataFieldCombiner create(DataFieldCombiner.Context context) {
    return  combinerMap.entrySet().stream()
      .filter(entry -> entry.getKey().test(context))
      .map(Map.Entry::getValue)
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
