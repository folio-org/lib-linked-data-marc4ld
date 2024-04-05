package org.folio.marc4ld.service.marc2ld.field;

import java.util.Collection;
import org.folio.ld.dictionary.model.Resource;

public interface FieldMapper {

  Collection<Resource> createResources(Resource parent);
}
