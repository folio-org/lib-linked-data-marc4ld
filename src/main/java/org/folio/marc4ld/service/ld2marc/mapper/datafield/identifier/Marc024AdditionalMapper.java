package org.folio.marc4ld.service.ld2marc.mapper.datafield.identifier;

import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_UNKNOWN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.util.Constants.EIGHT;

import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.mapper.AdditionalDataFieldsMapper;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Component;

@Component
public class Marc024AdditionalMapper implements AdditionalDataFieldsMapper {
  @Override
  public boolean test(ResourceEdge edge) {
    return edge.getSource() != null
      && edge.getSource().isOfType(INSTANCE)
      && edge.getPredicate() == MAP
      && edge.getTarget().isOfType(IDENTIFIER)
      && edge.getTarget().isOfType(ID_UNKNOWN);
  }

  @Override
  public DataField apply(ResourceEdge edge, DataField dataField) {
    dataField.setIndicator1(EIGHT);
    return dataField;
  }
}
