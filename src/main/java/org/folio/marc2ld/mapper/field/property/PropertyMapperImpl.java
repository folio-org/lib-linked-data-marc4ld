package org.folio.marc2ld.mapper.field.property;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.model.Resource;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PropertyMapperImpl implements PropertyMapper {
  private final ObjectMapper objectMapper;

  @Override
  public void mapProperties(Resource resource, DataField dataField, Marc2BibframeRules.FieldRule fieldRule,
                            Map<String, List<String>> properties) {
    var concatProperties = fieldRule.isConcatProperties();
    ofNullable(fieldRule.getSubfields()).ifPresent(sf -> sf.forEach((field, rule) -> {
      var subfield = dataField.getSubfield(field);
      if (nonNull(subfield)) {
        mapProperty(properties, rule, subfield.getData(), concatProperties);
      }
    }));
    mapProperty(properties, fieldRule.getInd1(), getIndicatorValue(dataField.getIndicator1()), concatProperties);
    mapProperty(properties, fieldRule.getInd2(), getIndicatorValue(dataField.getIndicator2()), concatProperties);

    ofNullable(fieldRule.getConstants()).ifPresent(
      c -> c.forEach((field, value) -> mapConstant(properties, field, value)));

    resource.setDoc(getJsonNode(properties));
  }

  private void mapProperty(Map<String, List<String>> properties, String rule, final String value, boolean concat) {
    ofNullable(rule).ifPresent(
      r -> ofNullable(value).map(String::strip).filter(StringUtils::isNotEmpty).ifPresent(v -> {
        var key = PropertyDictionary.valueOf(r).getValue();
        var keyProperties = properties.computeIfAbsent(key, k -> new ArrayList<>());
        if (concat && !keyProperties.isEmpty()) {
          var concatenated = keyProperties.get(0).concat(SPACE).concat(v);
          keyProperties.set(0, concatenated);
        } else {
          keyProperties.add(v);
        }
      }));
  }

  private String getIndicatorValue(char indicatorValue) {
    return String.valueOf(isNotEmptyIndicator(indicatorValue) ? indicatorValue : EMPTY);
  }

  private boolean isNotEmptyIndicator(char indicator) {
    return !Character.isSpaceChar(indicator) && indicator != Character.MIN_VALUE;
  }

  private void mapConstant(Map<String, List<String>> properties, String field, String value) {
    properties.put(PropertyDictionary.valueOf(field).getValue(), List.of(value));
  }

  private JsonNode getJsonNode(Map<String, ?> map) {
    return objectMapper.convertValue(map, JsonNode.class);
  }
}
