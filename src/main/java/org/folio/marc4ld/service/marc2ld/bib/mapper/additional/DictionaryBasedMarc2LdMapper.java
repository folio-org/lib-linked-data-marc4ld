package org.folio.marc4ld.service.marc2ld.bib.mapper.additional;

import static java.util.Collections.emptyMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.mapper.AdditionalMapper;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;

public abstract class DictionaryBasedMarc2LdMapper implements AdditionalMapper {

  private final DictionaryProcessor dictionaryProcessor;
  private final MapperHelper mapperHelper;

  DictionaryBasedMarc2LdMapper(DictionaryProcessor dictionaryProcessor, MapperHelper mapperHelper) {
    this.dictionaryProcessor = dictionaryProcessor;
    this.mapperHelper = mapperHelper;
  }

  @Override
  public void map(MarcData marcData, Resource resource) {
    getMappingConfigs()
      .stream()
      .map(mc -> mapSingle(resource, mc))
      .forEach(properties -> resource.setDoc(mapperHelper.getJsonNode(properties)));
  }

  private Map<String, List<String>> mapSingle(Resource resource, MappingConfig config) {
    var properties = mapperHelper.getProperties(resource);
    var codes = properties.getOrDefault(config.keyProperty.getValue(), List.of());
    var values = dictionaryProcessor.getValues(config.dictionaryName, codes);
    if (values.isEmpty()) {
      return emptyMap();
    }
    properties.put(config.valueProperty.getValue(), values);
    return properties;
  }

  abstract Set<MappingConfig> getMappingConfigs();

  record MappingConfig(String dictionaryName, PropertyDictionary keyProperty, PropertyDictionary valueProperty) {
  }
}
