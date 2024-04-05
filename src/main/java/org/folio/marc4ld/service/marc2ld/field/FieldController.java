package org.folio.marc4ld.service.marc2ld.field;

import java.util.List;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.marc2ld.Marc2ldFieldRule;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

public interface FieldController {

  void handleField(Resource parent, DataField dataField, List<ControlField> controlFields, Marc2ldFieldRule fieldRule);

}
