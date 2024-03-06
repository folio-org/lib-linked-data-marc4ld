package org.folio.marc4ld.service.mapper;

import java.util.List;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.dto.MarcData;
import org.marc4j.marc.DataField;

public interface Marc4ldMapper {

  String getTag();

  boolean canMap2ld(PredicateDictionary predicate);

  void map2ld(MarcData marcData, Resource resource);

  boolean canMap2Marc(PredicateDictionary predicate, Resource resource);

  List<DataField> map2marc(Resource resource);

}
