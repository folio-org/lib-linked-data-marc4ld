package org.folio.marc4ld.service.marc2ld.mapper.mapper.classification;

import static org.folio.ld.dictionary.PredicateDictionary.CLASSIFICATION;

import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.marc4ld.service.marc2ld.mapper.Marc2ldMapper;

public abstract class AbstractClassificationMapper implements Marc2ldMapper {

  private final String tag;

  protected AbstractClassificationMapper(String tag) {
    this.tag = tag;
  }

  @Override
  public String getTag() {
    return tag;
  }

  @Override
  public boolean canMap(PredicateDictionary predicate) {
    return predicate == CLASSIFICATION;
  }
}
