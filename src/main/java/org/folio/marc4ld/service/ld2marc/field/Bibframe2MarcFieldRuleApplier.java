package org.folio.marc4ld.service.ld2marc.field;

import java.util.Collection;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.field.param.ControlFieldParameter;
import org.folio.marc4ld.service.ld2marc.field.param.SubFieldParameter;

public interface Bibframe2MarcFieldRuleApplier {

  String getTag();

  boolean isSuitable(ResourceEdge edge);

  boolean isDataFieldCreatable(Resource resource);

  char getInd1(Resource resource);

  char getInd2(Resource resource);

  Collection<SubFieldParameter> getSubFields(Resource resource);

  Collection<ControlFieldParameter> getControlFields(Resource resource);
}
