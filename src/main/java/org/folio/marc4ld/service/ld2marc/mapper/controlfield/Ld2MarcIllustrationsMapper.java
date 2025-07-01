package org.folio.marc4ld.service.ld2marc.mapper.controlfield;

import static org.folio.ld.dictionary.PredicateDictionary.ILLUSTRATIONS;

import org.folio.ld.dictionary.PredicateDictionary;
import org.springframework.stereotype.Component;

@Component
public class Ld2MarcIllustrationsMapper extends AbstractLd2MarcBookMapper {

  @Override
  protected PredicateDictionary getPredicate() {
    return ILLUSTRATIONS;
  }

  @Override
  protected int getStartIndex() {
    return 18;
  }

  @Override
  protected int getEndIndex() {
    return 22;
  }
}
