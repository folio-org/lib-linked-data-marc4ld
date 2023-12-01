package org.folio.marc2ld.mapper.field.property;

import java.util.List;
import java.util.Map;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.model.Resource;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

public interface PropertyMapper {

  Map<String, List<String>> mapProperties(Resource resource, DataField dataField,
                                          Marc2BibframeRules.FieldRule fieldRule,
                                          List<ControlField> controlFields, Map<String, List<String>> properties);

}
