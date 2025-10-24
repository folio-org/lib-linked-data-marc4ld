package org.folio.marc4ld.service.marc2ld.postprocessor;

import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.Record;

public interface Marc2LdPostProcessor {
  void process(Resource instance, Record marcRecord);
}
