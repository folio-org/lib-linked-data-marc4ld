package org.folio.marc4ld.service.mapper.impl;

import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.folio.ld.dictionary.PredicateDictionary.CLASSIFICATION;
import static org.folio.ld.dictionary.PropertyDictionary.ASSIGNER;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.ITEM_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.STATUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc4ld.model.Resource;
import org.folio.marc4ld.service.mapper.Marc4ldMapper;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LcClassificationMapper implements Marc4ldMapper {

  private static final String TAG = "050";
  private static final String SOURCE = "lc";
  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(CATEGORY);
  private static final String UBA = "http://id.loc.gov/vocabulary/mstatus/uba";
  private static final String NUBA = "http://id.loc.gov/vocabulary/mstatus/nuba";
  private static final char ZERO = '0';
  private static final char ONE = '1';

  private final ObjectMapper objectMapper;
  private final MarcFactory marcFactory;

  @Override
  public String getTag() {
    return TAG;
  }

  @Override
  public boolean canMap(PredicateDictionary predicate, Resource resource) {
    return predicate == CLASSIFICATION
      && Objects.equals(resource.getTypes(), SUPPORTED_TYPES)
      && isLcClassification(resource);
  }

  @Override
  public void map2ld(DataField dataField, Resource resource) {
    var properties = (HashMap<String, List<String>>) objectMapper.convertValue(resource.getDoc(), HashMap.class);
    if (dataField.getIndicator1() == ZERO) {
      properties.put(STATUS.getValue(), List.of(UBA));
    } else if (dataField.getIndicator1() == ONE) {
      properties.put(STATUS.getValue(), List.of(NUBA));
    }
    if (dataField.getIndicator2() == ZERO) {
      properties.put(ASSIGNER.getValue(), List.of("http://id.loc.gov/vocabulary/organizations/dlc"));
    }
    resource.setDoc(objectMapper.convertValue(properties, JsonNode.class));
  }

  @Override
  public List<DataField> map2marc(Resource resource) {
    DataField dataField = null;
    var ind2 = resource.getDoc().get(ASSIGNER.getValue()) != null ? ZERO : SPACE.charAt(0);
    dataField = marcFactory.newDataField(TAG, getIndicator1(resource), ind2);
    dataField.addSubfield(marcFactory.newSubfield('a', resource.getDoc().get(CODE.getValue()).get(0).asText()));
    dataField.addSubfield(marcFactory.newSubfield('b', resource.getDoc().get(ITEM_NUMBER.getValue()).get(0).asText()));
    return List.of(dataField);
  }

  private boolean isLcClassification(Resource resource) {
    return SOURCE.equals(resource.getDoc().get(PropertyDictionary.SOURCE.getValue()).get(0).asText());
  }

  private char getIndicator1(Resource resource) {
    var ind1 = SPACE.charAt(0);
    if (resource.getDoc().get(STATUS.getValue()) != null) {
      var status = resource.getDoc().get(STATUS.getValue()).get(0).asText();
      if (UBA.equals(status)) {
        ind1 = ZERO;
      } else if (NUBA.equals(status)) {
        ind1 = ONE;
      }
    }
    return ind1;
  }
}
