package org.folio.marc4ld.service.marc2ld.mapper.mapper;

import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_AREA_CODE;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.marc4ld.util.Constants.GEOGRAPHIC_CODE_TO_NAME_DICTIONARY;

import java.util.List;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.springframework.stereotype.Component;

@Component
public class GeographicCoverageMapper extends DictionaryBasedMarc2LdMapper {

  private static final List<String> TAGS = List.of("043");
  private static final MappingConfig CONFIG = new MappingConfig(GEOGRAPHIC_CODE_TO_NAME_DICTIONARY,
    GEOGRAPHIC_AREA_CODE, NAME);

  GeographicCoverageMapper(DictionaryProcessor dictionaryProcessor, MapperHelper mapperHelper) {
    super(dictionaryProcessor, mapperHelper);
  }

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  @Override
  public boolean canMap(PredicateDictionary predicate) {
    return predicate == PredicateDictionary.GEOGRAPHIC_COVERAGE;
  }

  @Override
  MappingConfig getMappingConfig() {
    return CONFIG;
  }
}
