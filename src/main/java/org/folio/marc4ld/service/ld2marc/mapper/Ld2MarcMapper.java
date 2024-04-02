package org.folio.marc4ld.service.ld2marc.mapper;

import java.util.List;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.DataField;

public interface Ld2MarcMapper {

  boolean canMap(PredicateDictionary predicate, Resource resource);

  List<DataField> map(Resource resource);
}
