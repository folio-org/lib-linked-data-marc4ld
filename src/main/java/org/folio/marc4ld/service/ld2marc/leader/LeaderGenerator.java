package org.folio.marc4ld.service.ld2marc.leader;

import org.marc4j.marc.Record;

public interface LeaderGenerator {

  void addLeader(Record marcRecord);
}
