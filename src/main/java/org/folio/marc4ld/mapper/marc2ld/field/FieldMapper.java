package org.folio.marc4ld.mapper.marc2ld.field;

import java.util.List;
import org.folio.marc4ld.configuration.property.Marc2BibframeRules;
import org.folio.marc4ld.model.Resource;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

public interface FieldMapper {

  void handleField(Resource parent, DataField dataField, List<ControlField> controlFields,
                   Marc2BibframeRules.FieldRule fieldRule);

}
