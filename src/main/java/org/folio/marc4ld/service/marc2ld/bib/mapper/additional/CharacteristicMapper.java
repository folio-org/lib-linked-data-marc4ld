package org.folio.marc4ld.service.marc2ld.bib.mapper.additional;

import static org.folio.ld.dictionary.PredicateDictionary.CHARACTERISTIC;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.TERM;
import static org.folio.marc4ld.util.Constants.Dictionary.CHARACTERISTIC_CODE_TO_LINK;
import static org.folio.marc4ld.util.Constants.Dictionary.CHARACTERISTIC_CODE_TO_TERM;
import static org.folio.marc4ld.util.Constants.TAG_008;

import java.util.List;
import java.util.Set;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.springframework.stereotype.Component;

@Component
public class CharacteristicMapper extends DictionaryBasedMarc2LdMapper {

  private static final List<String> TAGS = List.of(TAG_008);
  private static final MappingConfig CONFIG_LINK = new MappingConfig(CHARACTERISTIC_CODE_TO_LINK, CODE, LINK);
  private static final MappingConfig CONFIG_TERM = new MappingConfig(CHARACTERISTIC_CODE_TO_TERM, CODE, TERM);

  CharacteristicMapper(DictionaryProcessor dictionaryProcessor, MapperHelper mapperHelper) {
    super(dictionaryProcessor, mapperHelper);
  }

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  @Override
  public boolean canMap(Marc4LdRules.FieldRule fieldRule) {
    return CHARACTERISTIC.name().equals(fieldRule.getPredicate());
  }

  @Override
  Set<MappingConfig> getMappingConfigs() {
    return Set.of(CONFIG_LINK, CONFIG_TERM);
  }

}
