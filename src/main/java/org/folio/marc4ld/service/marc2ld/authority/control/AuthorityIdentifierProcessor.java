package org.folio.marc4ld.service.marc2ld.authority.control;

import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.DataField;

public interface AuthorityIdentifierProcessor {

  void setIdentifier(Resource resource, DataField dataField);
}
