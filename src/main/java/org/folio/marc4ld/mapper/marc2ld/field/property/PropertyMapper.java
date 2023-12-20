package org.folio.marc4ld.mapper.marc2ld.field.property;

import java.util.List;
import java.util.Map;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.model.Resource;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

public interface PropertyMapper {

  Map<String, List<String>> mapProperties(Resource resource, DataField dataField,
                                          Marc4BibframeRules.FieldRule fieldRule,
                                          List<ControlField> controlFields, Map<String, List<String>> properties);

}
