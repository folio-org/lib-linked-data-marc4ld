package org.folio.marc4ld.service.marc2ld.field;

import java.util.Collection;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.marc2ld.Marc2ldFieldRuleApplier;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;

public interface ResourceProcessor {

  Collection<Resource> create(DataField field, Record marcRecord, Marc2ldFieldRuleApplier rule);

  void handleField(Resource parent, DataField field, Record marcRecord, Marc2ldFieldRuleApplier rule);
}
