package org.folio.marc4ld.service.ld2marc.mapper.datafield.classification;

import static org.folio.ld.dictionary.PredicateDictionary.ASSIGNING_SOURCE;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.marc4ld.util.Constants.Classification.DLC;
import static org.folio.marc4ld.util.Constants.Classification.LC;
import static org.folio.marc4ld.util.Constants.Classification.NUBA;
import static org.folio.marc4ld.util.Constants.Classification.TAG_050;
import static org.folio.marc4ld.util.Constants.Classification.UBA;
import static org.folio.marc4ld.util.Constants.FOUR;
import static org.folio.marc4ld.util.Constants.ONE;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.ZERO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.MarcFactory;
import org.springframework.stereotype.Component;

@Component
public class Ld2MarcLcClassificationMapper extends AbstractClassificationMapper {

  public Ld2MarcLcClassificationMapper(MarcFactory marcFactory, ObjectMapper objectMapper) {
    super(objectMapper, marcFactory, TAG_050, LC);
  }

  @Override
  protected char getIndicator1(Resource resource) {
    if (hasLinkInEdge(resource, STATUS, UBA)) {
      return ZERO;
    } else if (hasLinkInEdge(resource, STATUS, NUBA)) {
      return ONE;
    }
    return SPACE;
  }

  @Override
  protected char getIndicator2(Resource resource) {
    return hasLinkInEdge(resource, ASSIGNING_SOURCE, DLC) ? ZERO : FOUR;
  }
}
