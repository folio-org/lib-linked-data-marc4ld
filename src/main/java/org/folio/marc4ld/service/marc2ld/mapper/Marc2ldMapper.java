package org.folio.marc4ld.service.marc2ld.mapper;

import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.dto.MarcData;

public interface Marc2ldMapper {

  String getTag();

  boolean canMap(PredicateDictionary predicate);

  void map(MarcData marcData, Resource resource);
}
