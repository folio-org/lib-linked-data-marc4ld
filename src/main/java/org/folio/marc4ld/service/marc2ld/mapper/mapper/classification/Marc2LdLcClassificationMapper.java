package org.folio.marc4ld.service.marc2ld.mapper.mapper.classification;

import static org.folio.ld.dictionary.PropertyDictionary.ASSIGNER;
import static org.folio.ld.dictionary.PropertyDictionary.STATUS;
import static org.folio.marc4ld.util.Constants.Classification.DLC;
import static org.folio.marc4ld.util.Constants.Classification.NUBA;
import static org.folio.marc4ld.util.Constants.Classification.TAG_050;
import static org.folio.marc4ld.util.Constants.Classification.UBA;
import static org.folio.marc4ld.util.Constants.ONE;
import static org.folio.marc4ld.util.Constants.ZERO;

import java.util.List;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.marc2ld.mapper.mapper.MapperHelper;
import org.springframework.stereotype.Component;

@Component
public class Marc2LdLcClassificationMapper extends AbstractClassificationMapper {

  private final MapperHelper mapperHelper;

  protected Marc2LdLcClassificationMapper(MapperHelper mapperHelper) {
    super(TAG_050);
    this.mapperHelper = mapperHelper;
  }

  @Override
  public void map(MarcData marcData, Resource resource) {
    var dataField = marcData.getDataField();
    var properties = mapperHelper.getProperties(resource);
    if (dataField.getIndicator1() == ZERO) {
      properties.put(STATUS.getValue(), List.of(UBA));
    } else if (dataField.getIndicator1() == ONE) {
      properties.put(STATUS.getValue(), List.of(NUBA));
    }
    if (dataField.getIndicator2() == ZERO) {
      properties.put(ASSIGNER.getValue(), List.of(DLC));
    }
    resource.setDoc(mapperHelper.getJsonNode(properties));
  }
}
