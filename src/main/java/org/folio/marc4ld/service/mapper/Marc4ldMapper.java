package org.folio.marc4ld.service.mapper;

import java.util.List;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.marc4ld.model.Resource;
import org.marc4j.marc.DataField;

public interface Marc4ldMapper {

  String getTag();

  void map2ld(DataField dataField, Resource resource);

  boolean canMap(PredicateDictionary predicate, Resource resource);

  List<DataField> map2marc(Resource resource);
}
