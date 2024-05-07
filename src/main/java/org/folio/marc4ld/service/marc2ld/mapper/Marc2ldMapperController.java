package org.folio.marc4ld.service.marc2ld.mapper;

import java.util.Collection;
import java.util.List;
import org.marc4j.marc.ControlField;

public interface Marc2ldMapperController {

  Collection<Marc2ldMapper> findAll(List<ControlField> controlFields);

  Collection<Marc2ldMapper> findAll(String tag);
}
