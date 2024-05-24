package org.folio.marc4ld.service.marc2ld.field;

import java.util.Collection;
import java.util.List;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.marc2ld.Marc2ldFieldRuleApplier;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

public interface ResourceProcessor {

  Collection<Resource> create(DataField field, List<ControlField> controlFields, Marc2ldFieldRuleApplier rule);

  void handleField(Resource parent, DataField field, List<ControlField> controlFields, Marc2ldFieldRuleApplier rule);
}
