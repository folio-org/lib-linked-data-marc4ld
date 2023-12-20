package org.folio.marc4ld.mapper.marc2ld.field.property;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.folio.ld.dictionary.PropertyDictionary.valueOf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.dictionary.DictionaryProcessor;
import org.folio.marc4ld.model.Resource;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PropertyMapperImpl implements PropertyMapper {
  private static final String PERCENT = "%";
  private final ObjectMapper objectMapper;
  private final DictionaryProcessor dictionaryProcessor;

  @Override
  public Map<String, List<String>> mapProperties(Resource resource, DataField dataField,
                                                 Marc4BibframeRules.FieldRule fieldRule,
                                                 List<ControlField> controlFields,
                                                 Map<String, List<String>> properties) {
    ofNullable(fieldRule.getSubfields()).ifPresent(sf -> sf.forEach((field, rule) -> {
      var subfield = dataField.getSubfield(field);
      if (nonNull(subfield)) {
        mapProperty(properties, rule, subfield.getData(), fieldRule.getConcat());
      }
    }));
    mapProperty(properties, fieldRule.getInd1(), getIndicatorValue(dataField.getIndicator1()), fieldRule.getConcat());
    mapProperty(properties, fieldRule.getInd2(), getIndicatorValue(dataField.getIndicator2()), fieldRule.getConcat());

    ofNullable(fieldRule.getControlFields()).ifPresent(controlFieldRules ->
      ofNullable(controlFields).ifPresent(cfs -> controlFieldRules
        .forEach((cfRuleTag, cfRules) -> cfs.stream()
          .filter(cf -> cf.getTag().equals(cfRuleTag))
          .forEach(
            cf -> cfRules.forEach((property, range) -> mapControlField(properties, cf.getData(), range, property)))
        ))
    );

    ofNullable(fieldRule.getConstants()).ifPresent(
      c -> c.forEach((field, value) -> mapConstant(properties, field, value)));

    resource.setDoc(getJsonNode(properties));
    return properties;
  }

  private void mapProperty(Map<String, List<String>> properties, String rule, final String value, String concat) {
    ofNullable(rule).ifPresent(
      r -> ofNullable(value).map(String::trim).filter(StringUtils::isNotEmpty).ifPresent(v -> {
        var key = valueOf(r).getValue();
        var keyProperties = properties.computeIfAbsent(key, k -> new ArrayList<>());
        if (!keyProperties.isEmpty()) {
          var concatenated = keyProperties.get(0).concat(ofNullable(concat).orElse(EMPTY)).concat(v);
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
    if (value.contains(PERCENT)) {
      var propertyEnumName = value.substring(value.indexOf(PERCENT) + 1);
      var propertyName = valueOf(propertyEnumName).getValue();
      ofNullable(properties.get(propertyName)).map(propValues -> propValues
          .stream()
          .map(prop -> value.substring(0, value.indexOf(PERCENT)) + prop)
          .collect(Collectors.toCollection(LinkedHashSet::new)))
        .ifPresent(values -> properties.put(valueOf(field).getValue(), new ArrayList<>(values)));
    } else {
      properties.put(valueOf(field).getValue(), List.of(value));
    }
  }

  private void mapControlField(Map<String, List<String>> properties, String cfValue, List<Integer> range,
                               String property) {
    var value = cfValue.strip().substring(range.get(0), range.get(1));
    dictionaryProcessor.getValue(property, value).ifPresentOrElse(
      v -> properties.put(valueOf(property).getValue(), List.of(v)),
      () -> properties.put(valueOf(property).getValue(), List.of(value)));
  }

  private JsonNode getJsonNode(Map<String, ?> map) {
    return objectMapper.convertValue(map, JsonNode.class);
  }
}
