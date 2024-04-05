package org.folio.marc4ld.service.ld2marc.processing.combine;

import java.util.Collection;
import org.marc4j.marc.DataField;

public interface DataFieldCombiner {

  Collection<DataField> build();

  void add(DataField dataField);
}
