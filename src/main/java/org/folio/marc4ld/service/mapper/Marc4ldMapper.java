package org.folio.marc4ld.service.mapper;

import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.model.Resource;
import org.marc4j.marc.DataField;

public interface Marc4ldMapper {

  String getTag();

  boolean canMap(String tag, PredicateDictionary predicate, Resource resource);

  void map2ld(DataField dataField, Resource resource);

  DataField map2marc(String tag, Marc4BibframeRules.FieldRule fieldRule, Resource resource);
}
