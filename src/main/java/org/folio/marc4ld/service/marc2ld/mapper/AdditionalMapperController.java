package org.folio.marc4ld.service.marc2ld.mapper;

import java.util.Collection;
import java.util.List;
import org.marc4j.marc.ControlField;

public interface AdditionalMapperController {

  Collection<AdditionalMapper> findAll(List<ControlField> controlFields);

  Collection<AdditionalMapper> findAll(String tag);
}
