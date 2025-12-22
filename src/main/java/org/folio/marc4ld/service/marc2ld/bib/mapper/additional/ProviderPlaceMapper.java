package org.folio.marc4ld.service.marc2ld.bib.mapper.additional;

import static org.folio.ld.dictionary.PredicateDictionary.PROVIDER_PLACE;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.specific.PlaceDictionary;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.marc2ld.mapper.AdditionalMapper;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProviderPlaceMapper implements AdditionalMapper {
  private static final String UNKNOWN_PLACE_CODE = "xx";
  private static final String PLACE_URI_PREFIX = "http://id.loc.gov/vocabulary/countries/";

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
  public void map(MarcData marcData, Resource mappedSofar) {
    var properties = mapperHelper.getProperties(mappedSofar);
    var placeCodes = properties.getOrDefault(CODE.getValue(), List.of());

    if (placeCodes.isEmpty()) {
      return;
    }

    var placeNames = getPlaceNames(placeCodes);
    if (placeNames.isEmpty()) {
      properties.put(CODE.getValue(), List.of(UNKNOWN_PLACE_CODE));
      properties.put(LINK.getValue(), List.of(PLACE_URI_PREFIX + UNKNOWN_PLACE_CODE));
      properties.put(NAME.getValue(), getPlaceNames(List.of(UNKNOWN_PLACE_CODE)));
    } else {
      properties.put(NAME.getValue(), placeNames);
    }

    mappedSofar.setDoc(mapperHelper.getJsonNode(properties));
  }

  private static List<String> getPlaceNames(List<String> placeCodes) {
    return placeCodes.stream()
      .map(PlaceDictionary::getValue)
      .flatMap(Optional::stream)
      .toList();
  }
}
