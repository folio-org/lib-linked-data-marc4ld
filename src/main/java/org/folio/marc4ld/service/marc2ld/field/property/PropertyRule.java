package org.folio.marc4ld.service.marc2ld.field.property;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

public interface PropertyRule {

  Collection<Map<String, List<String>>> create(
    DataField dataField,
    Collection<ControlField> fields
  );

  Map<String, List<String>> merge(
    DataField dataField,
    Collection<ControlField> fields,
    Map<String, List<String>> values
  );
}
