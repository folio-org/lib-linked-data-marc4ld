package org.folio.marc2ld.mapper.field;

import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.model.Resource;
import org.marc4j.marc.DataField;

public interface FieldMapper {

  void handleField(Resource instance, DataField dataField, Marc2BibframeRules.FieldRule fieldRule);

}
