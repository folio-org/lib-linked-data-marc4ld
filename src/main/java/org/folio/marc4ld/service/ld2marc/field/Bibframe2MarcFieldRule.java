package org.folio.marc4ld.service.ld2marc.field;

import java.util.Collection;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.field.param.ControlFieldParameter;
import org.folio.marc4ld.service.ld2marc.field.param.SubFieldParameter;

public interface Bibframe2MarcFieldRule {

  String getTag();

  boolean isSuitable(Resource resource, PredicateDictionary predicate);

  boolean isDataFieldCreatable(Resource resource);

  char getInd1(Resource resource);

  char getInd2(Resource resource);

  Collection<SubFieldParameter> getSubFields(Resource resource);

  Collection<ControlFieldParameter> getControlFields(Resource resource);
}
