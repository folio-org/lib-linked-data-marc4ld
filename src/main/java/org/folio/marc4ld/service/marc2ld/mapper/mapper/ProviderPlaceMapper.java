package org.folio.marc4ld.service.marc2ld.mapper.mapper;

import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;

import java.util.List;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.springframework.stereotype.Component;

@Component
public class ProviderPlaceMapper extends DictionaryBasedMarc2LdMapper {

  private static final List<String> TAGS = List.of("260", "261", "262", "264");
  private static final String PLACE_CODE_TO_NAME_DICTIONARY = "PLACE_CODE_TO_NAME";
  private static final MappingConfig MAPPING_CONFIG = new MappingConfig(PLACE_CODE_TO_NAME_DICTIONARY, CODE, NAME);

  ProviderPlaceMapper(DictionaryProcessor dictionaryProcessor, MapperHelper mapperHelper) {
    super(dictionaryProcessor, mapperHelper);
  }

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  @Override
  public boolean canMap(PredicateDictionary predicate) {
    return predicate == PredicateDictionary.PROVIDER_PLACE;
  }

  @Override
  MappingConfig getMappingConfig() {
    return MAPPING_CONFIG;
  }
}
