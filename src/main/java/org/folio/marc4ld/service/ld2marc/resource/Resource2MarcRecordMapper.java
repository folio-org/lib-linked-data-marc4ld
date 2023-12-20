package org.folio.marc4ld.service.ld2marc.resource;

import org.folio.marc4ld.model.Resource;
import org.marc4j.marc.Record;

public interface Resource2MarcRecordMapper {

  Record toMarcRecord(Resource resource);

}
