package org.folio.marc2ld.mapper.field.property;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.folio.marc2ld.util.BibframeUtil.isNotEmptyIndicator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
    boolean concatProperties = fieldRule.isConcatProperties();
    fieldRule.getSubfields().forEach((field, rule) -> {
      var subfield = dataField.getSubfield(field);
      if (nonNull(subfield)) {
        mapProperty(properties, rule, subfield.getData(), concatProperties);
      }
    });
    mapProperty(properties, fieldRule.getInd1(), String.valueOf(isNotEmptyIndicator(dataField.getIndicator1())
      ? dataField.getIndicator1() : ""), concatProperties);
    mapProperty(properties, fieldRule.getInd2(), String.valueOf(isNotEmptyIndicator(dataField.getIndicator2())
      ? dataField.getIndicator2() : ""), concatProperties);
    resource.setDoc(getJsonNode(properties));
  }

  private void mapProperty(Map<String, List<String>> properties, String rule, String value, boolean concat) {
    if (nonNull(rule) && nonNull(value)) {
      value = value.strip();
      if (isNotEmpty(value)) {
        var key = PropertyDictionary.valueOf(rule).getValue();
        var keyProperties = properties.computeIfAbsent(key, k -> new ArrayList<>());
        if (concat && !keyProperties.isEmpty()) {
          var concatenated = keyProperties.get(0).concat(SPACE).concat(value);
          keyProperties.set(0, concatenated);
        } else {
          keyProperties.add(value);
        }
      }
    }
  }

  private JsonNode getJsonNode(Map<String, ?> map) {
    return objectMapper.convertValue(map, JsonNode.class);
  }
}
