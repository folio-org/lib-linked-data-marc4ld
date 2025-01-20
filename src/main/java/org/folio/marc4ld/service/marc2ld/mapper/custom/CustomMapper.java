package org.folio.marc4ld.service.marc2ld.mapper.custom;

import org.folio.ld.dictionary.model.Resource;

public interface CustomMapper {

  boolean isApplicable(org.marc4j.marc.Record marcRecord);

  void map(org.marc4j.marc.Record marcRecord, Resource instance);
}
