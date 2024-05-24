package org.folio.marc4ld.service.marc2ld.mapper.mapper.classification;

import static org.folio.ld.dictionary.PropertyDictionary.EDITION;
import static org.folio.marc4ld.util.Constants.Classification.ABRIDGED;
import static org.folio.marc4ld.util.Constants.Classification.FULL;
import static org.folio.marc4ld.util.Constants.Classification.TAG_082;
import static org.folio.marc4ld.util.Constants.ONE;
import static org.folio.marc4ld.util.Constants.ZERO;

import java.util.List;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.marc2ld.mapper.mapper.MapperHelper;
import org.springframework.stereotype.Component;

@Component
public class Marc2LdDdcClassificationMapper extends AbstractClassificationMapper {

  private final MapperHelper mapperHelper;

  protected Marc2LdDdcClassificationMapper(MapperHelper mapperHelper) {
    super(TAG_082);
    this.mapperHelper = mapperHelper;
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
