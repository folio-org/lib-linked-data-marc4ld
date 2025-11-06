package org.folio.marc4ld.service.marc2ld.bib.mapper.additional;

import static org.folio.ld.dictionary.PredicateDictionary.CLASSIFICATION;
import static org.folio.ld.dictionary.PropertyDictionary.EDITION;
import static org.folio.marc4ld.util.Constants.Classification.ABRIDGED;
import static org.folio.marc4ld.util.Constants.Classification.FULL;
import static org.folio.marc4ld.util.Constants.Classification.TAG_082;
import static org.folio.marc4ld.util.Constants.ONE;
import static org.folio.marc4ld.util.Constants.ZERO;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.marc2ld.mapper.AdditionalMapper;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DdcClassificationMapper implements AdditionalMapper {

  private static final List<String> TAGS = List.of(TAG_082);

  private final MapperHelper mapperHelper;

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  @Override
  public boolean canMap(Marc4LdRules.FieldRule fieldRule) {
    return CLASSIFICATION.name().equals(fieldRule.getPredicate());
  }

  @Override
  public void map(MarcData marcData, Resource mappedSofar) {
    var dataField = marcData.getDataField();
    var properties = mapperHelper.getProperties(mappedSofar);
    if (dataField.getIndicator1() == ZERO) {
      properties.put(EDITION.getValue(), List.of(FULL));
    } else if (dataField.getIndicator1() == ONE) {
      properties.put(EDITION.getValue(), List.of(ABRIDGED));
    }
    mappedSofar.setDoc(mapperHelper.getJsonNode(properties));
  }
}
