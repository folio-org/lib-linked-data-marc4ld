package org.folio.marc4ld.service.marc2ld.bib.mapper.additional;

import static org.folio.ld.dictionary.PredicateDictionary.PROVIDER_PLACE;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PlaceDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.marc2ld.mapper.AdditionalMapper;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProviderPlaceMapper implements AdditionalMapper {
  private static final List<String> TAGS = List.of("260", "261", "262", "264");
  private final MapperHelper mapperHelper;

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  @Override
  public boolean canMap(Marc4LdRules.FieldRule fieldRule) {
    return PROVIDER_PLACE.name().equals(fieldRule.getPredicate());
  }

  @Override
  public void map(MarcData marcData, Resource resource) {
    var properties = mapperHelper.getProperties(resource);
    var codes = properties.getOrDefault(CODE.getValue(), List.of());
    var values = codes.stream()
      .map(PlaceDictionary::getName)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .toList();
    if (values.isEmpty()) {
      return;
    }
    properties.put(NAME.getValue(), values);
    resource.setDoc(mapperHelper.getJsonNode(properties));
  }

}
