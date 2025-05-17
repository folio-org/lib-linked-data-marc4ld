package org.folio.marc4ld.service.marc2ld.bib.mapper.additional;

import java.util.List;
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
    MappingConfig config = getMappingConfig();
    var properties = mapperHelper.getProperties(resource);
    var codes = properties.getOrDefault(config.keyProperty.getValue(), List.of());
    var values = dictionaryProcessor.getValues(config.dictionaryName, codes);
    if (values.isEmpty()) {
      return;
    }
    properties.put(config.valueProperty.getValue(), values);
    resource.setDoc(mapperHelper.getJsonNode(properties));
  }

  abstract MappingConfig getMappingConfig();

  record MappingConfig(String dictionaryName, PropertyDictionary keyProperty, PropertyDictionary valueProperty) {
  }
}
