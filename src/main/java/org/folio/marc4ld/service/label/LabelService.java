package org.folio.marc4ld.service.label;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.model.Resource;

public interface LabelService {

  void setLabel(Resource resource, Map<String, List<String>> properties);
}
