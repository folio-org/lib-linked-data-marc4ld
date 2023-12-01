package org.folio.marc2ld.mapper.field;

import java.util.List;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.model.Resource;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

public interface FieldMapper {

  void handleField(Resource parent, DataField dataField, List<ControlField> controlFields,
                     Marc2BibframeRules.FieldRule fieldRule);

}
