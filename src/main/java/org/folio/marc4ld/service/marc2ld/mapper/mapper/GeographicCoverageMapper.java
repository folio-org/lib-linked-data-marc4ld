package org.folio.marc4ld.service.marc2ld.mapper.mapper;

import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_AREA_CODE;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.marc4ld.util.Constants.GEOGRAPHIC_CODE_TO_NAME_DICTIONARY;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.mapper.Marc2ldMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeographicCoverageMapper implements Marc2ldMapper {

  private static final String TAG_043 = "043";

  private final DictionaryProcessor dictionaryProcessor;
  private final MapperHelper mapperHelper;

  @Override
  public List<String> getTags() {
    return List.of(TAG_043);
  }

  @Override
  public boolean canMap(PredicateDictionary predicate) {
    return predicate == PredicateDictionary.GEOGRAPHIC_COVERAGE;
  }

  @Override
  public void map(MarcData marcData, Resource resource) {
    var properties = mapperHelper.getProperties(resource);
    var geographicCodes = properties.getOrDefault(GEOGRAPHIC_AREA_CODE.getValue(), List.of());
    var geographicNames = geographicCodes.stream()
      .map(code -> dictionaryProcessor.getValue(GEOGRAPHIC_CODE_TO_NAME_DICTIONARY, code))
      .flatMap(Optional::stream)
      .toList();
    properties.put(NAME.getValue(), geographicNames);
    resource.setDoc(mapperHelper.getJsonNode(properties));
  }
}
