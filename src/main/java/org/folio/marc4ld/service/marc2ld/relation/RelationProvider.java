package org.folio.marc4ld.service.marc2ld.relation;

import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.marc2ld.Marc2ldFieldRuleApplier;
import org.marc4j.marc.DataField;

public interface RelationProvider {

  void checkRelation(Resource source, Resource target, DataField dataField, Marc2ldFieldRuleApplier fieldRule);
}
