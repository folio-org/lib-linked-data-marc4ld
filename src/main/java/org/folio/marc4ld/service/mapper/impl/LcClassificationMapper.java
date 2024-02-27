package org.folio.marc4ld.service.mapper.impl;

import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.folio.ld.dictionary.PredicateDictionary.CLASSIFICATION;
import static org.folio.ld.dictionary.PropertyDictionary.ASSIGNER;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.ITEM_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.STATUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
  private static final String DLC = "http://id.loc.gov/vocabulary/organizations/dlc";
  private static final char ZERO = '0';
  private static final char ONE = '1';
  private static final char A = 'a';
  private static final char B = 'b';

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
    if (Objects.equals(resource.getTypes(), SUPPORTED_TYPES)) {
      var properties = objectMapper.convertValue(resource.getDoc(),
        new TypeReference<HashMap<String, List<String>>>() {});
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

  @Override
  public List<DataField> map2marc(Resource resource) {
    var dataField = marcFactory.newDataField(TAG, getIndicator1(resource), getIndicator2(resource));
    getCodes(resource).ifPresent(codes -> codes
      .forEach(code -> dataField.addSubfield(marcFactory.newSubfield(A, code))));
    getItemNumber(resource).ifPresent(in -> dataField.addSubfield(marcFactory.newSubfield(B, in)));
    return List.of(dataField);
  }

  private boolean isLcClassification(Resource resource) {
    return SOURCE.equals(resource.getDoc().get(PropertyDictionary.SOURCE.getValue()).get(0).asText());
  }

  private char getIndicator1(Resource resource) {
    var ind1 = SPACE.charAt(0);
    if (resource.getDoc().get(STATUS.getValue()) != null) {
      var statuses = objectMapper.convertValue(resource.getDoc().get(STATUS.getValue()), List.class);
      if (statuses.contains(UBA)) {
        ind1 = ZERO;
      } else if (statuses.contains(NUBA)) {
        ind1 = ONE;
      }
    }
    return ind1;
  }

  private char getIndicator2(Resource resource) {
    var ind2 = SPACE.charAt(0);
    if (resource.getDoc().get(ASSIGNER.getValue()) != null) {
      var assigners = objectMapper.convertValue(resource.getDoc().get(ASSIGNER.getValue()), List.class);
      if (assigners.contains(DLC)) {
        ind2 = ZERO;
      }
    }
    return ind2;
  }

  private Optional<List<String>> getCodes(Resource resource) {
    return resource.getDoc().get(CODE.getValue()) != null
      ? Optional.of(objectMapper.convertValue(resource.getDoc().get(CODE.getValue()), new TypeReference<>() {}))
      : Optional.empty();
  }

  private Optional<String> getItemNumber(Resource resource) {
    return resource.getDoc().get(ITEM_NUMBER.getValue()) != null
      ? Optional.of(resource.getDoc().get(ITEM_NUMBER.getValue()).get(0).asText())
      : Optional.empty();
  }
}
