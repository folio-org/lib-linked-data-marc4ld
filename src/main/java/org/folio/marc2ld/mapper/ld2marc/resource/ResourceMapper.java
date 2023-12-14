package org.folio.marc2ld.mapper.ld2marc.resource;

import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.marc2ld.model.Resource;
import org.marc4j.marc.Record;

public interface ResourceMapper {

  void handleResource(Resource resource, PredicateDictionary predicate, Record marcRecord);

}
