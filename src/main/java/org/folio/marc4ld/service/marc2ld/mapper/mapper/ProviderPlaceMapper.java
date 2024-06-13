package org.folio.marc4ld.service.marc2ld.mapper.mapper;

import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.mapper.Marc2ldMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProviderPlaceMapper implements Marc2ldMapper {
  private static final String TAG_261 = "261";
  private static final String TAG_262 = "262";
  private static final String TAG_264 = "264";
  private static final String PLACE_CODE_TO_NAME_DICTIONARY = "PLACE_CODE_TO_NAME";

  private final DictionaryProcessor dictionaryProcessor;
  private final MapperHelper mapperHelper;

  @Override
  public List<String> getTags() {
    return List.of(TAG_261, TAG_262, TAG_264);
  }

  @Override
  public boolean canMap(PredicateDictionary predicate) {
    return predicate == PredicateDictionary.PROVIDER_PLACE;
  }

  @Override
  public void map(MarcData marcData, Resource resource) {
    var properties = mapperHelper.getProperties(resource);
    var placeCodes = properties.getOrDefault(CODE.getValue(), List.of());
    var placeNames = dictionaryProcessor.getValues(PLACE_CODE_TO_NAME_DICTIONARY, placeCodes);
    if (placeNames.isEmpty()) {
      return;
    }
    properties.put(NAME.getValue(), placeNames);
    resource.setDoc(mapperHelper.getJsonNode(properties));
  }
}
