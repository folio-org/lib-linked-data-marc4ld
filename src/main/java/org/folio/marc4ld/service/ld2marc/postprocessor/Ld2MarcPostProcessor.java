package org.folio.marc4ld.service.ld2marc.postprocessor;

import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.Record;

public interface Ld2MarcPostProcessor {
  void postProcess(Resource instance, Record generatedMarc);
}
