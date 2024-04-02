package org.folio.marc4ld.service.marc2ld.mapper.mapper;

import static org.folio.ld.dictionary.PredicateDictionary.CLASSIFICATION;
import static org.folio.ld.dictionary.PropertyDictionary.ASSIGNER;
import static org.folio.ld.dictionary.PropertyDictionary.STATUS;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.marc2ld.mapper.Marc2ldMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClassificationMapper implements Marc2ldMapper {

  private static final String TAG = "050";
  private static final String UBA = "http://id.loc.gov/vocabulary/mstatus/uba";
  private static final String NUBA = "http://id.loc.gov/vocabulary/mstatus/nuba";
  private static final String DLC = "http://id.loc.gov/vocabulary/organizations/dlc";
  private static final char ZERO = '0';
  private static final char ONE = '1';

  private final ObjectMapper objectMapper;

  @Override
  public String getTag() {
    return TAG;
  }

  @Override
  public boolean canMap(PredicateDictionary predicate) {
    return predicate == CLASSIFICATION;
  }

  @Override
  public void map(MarcData marcData, Resource resource) {
    var dataField = marcData.getDataField();
    var properties = objectMapper.convertValue(resource.getDoc(),
      new TypeReference<HashMap<String, List<String>>>() {
      });
    if (dataField.getIndicator1() == ZERO) {
      properties.put(STATUS.getValue(), List.of(UBA));
    } else if (dataField.getIndicator1() == ONE) {
      properties.put(STATUS.getValue(), List.of(NUBA));
    }
    if (dataField.getIndicator2() == ZERO) {
      properties.put(ASSIGNER.getValue(), List.of(DLC));
    }
    resource.setDoc(objectMapper.convertValue(properties, JsonNode.class));
  }
}
