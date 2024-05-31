package org.folio.marc4ld.service.marc2ld.mapper.mapper;

import static org.folio.ld.dictionary.PredicateDictionary.CLASSIFICATION;
import static org.folio.ld.dictionary.PropertyDictionary.EDITION;
import static org.folio.marc4ld.util.Constants.Classification.ABRIDGED;
import static org.folio.marc4ld.util.Constants.Classification.FULL;
import static org.folio.marc4ld.util.Constants.Classification.TAG_082;
import static org.folio.marc4ld.util.Constants.ONE;
import static org.folio.marc4ld.util.Constants.ZERO;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.marc2ld.mapper.Marc2ldMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DdcClassificationMapper implements Marc2ldMapper {

  private final MapperHelper mapperHelper;

  @Override
  public String getTag() {
    return TAG_082;
  }

  @Override
  public boolean canMap(PredicateDictionary predicate) {
    return predicate == CLASSIFICATION;
  }

  @Override
  public void map(MarcData marcData, Resource resource) {
    var dataField = marcData.getDataField();
    var properties = mapperHelper.getProperties(resource);
    if (dataField.getIndicator1() == ZERO) {
      properties.put(EDITION.getValue(), List.of(FULL));
    } else if (dataField.getIndicator1() == ONE) {
      properties.put(EDITION.getValue(), List.of(ABRIDGED));
    }
    resource.setDoc(mapperHelper.getJsonNode(properties));
  }
}
