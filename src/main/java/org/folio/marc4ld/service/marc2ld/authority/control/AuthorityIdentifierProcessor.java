package org.folio.marc4ld.service.marc2ld.authority.control;

import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.ControlField;

public interface AuthorityIdentifierProcessor {

  void setIdentifier(Resource resource, ControlField controlField);
}
