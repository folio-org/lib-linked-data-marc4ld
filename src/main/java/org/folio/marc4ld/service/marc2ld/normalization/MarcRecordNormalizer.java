package org.folio.marc4ld.service.marc2ld.normalization;

import org.marc4j.marc.Record;

public interface MarcRecordNormalizer {

  void normalize(Record record);
}
