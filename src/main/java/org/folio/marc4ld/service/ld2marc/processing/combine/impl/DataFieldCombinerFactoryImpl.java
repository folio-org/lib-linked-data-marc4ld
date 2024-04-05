package org.folio.marc4ld.service.ld2marc.processing.combine.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombiner;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombinerFactory;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Component;

@Component
public class DataFieldCombinerFactoryImpl implements DataFieldCombinerFactory {

  @Override
  public DataFieldCombiner create(String tag) {
    return  switch (tag) {
      case "245" -> new TitleCombiner();
      default -> new DefaultDataFieldCombiner();
    };
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
