package org.folio.marc4ld.service.ld2marc.field.impl;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.ld2marc.field.ControlFieldRule;
import org.folio.marc4ld.service.ld2marc.field.param.ControlFieldParameter;

public class ControlFieldRuleImpl implements ControlFieldRule {

  private final String tag;
  private final Collection<ControlFieldSettings> controlFieldSettings;
  private final DictionaryProcessor dictionaryProcessor;

  public ControlFieldRuleImpl(String tag, Map<String, List<Integer>> rules, DictionaryProcessor dictionaryProcessor) {
    this.tag = tag;
    this.dictionaryProcessor = dictionaryProcessor;
    this.controlFieldSettings = rules.entrySet()
      .stream()
      .map(entry -> new ControlFieldSettings(
        entry.getKey(),
        PropertyDictionary.valueOf(entry.getKey()).getValue(),
        entry.getValue().get(0),
        entry.getValue().get(1))
      )
      .toList();
  }

  @Override
  public Collection<ControlFieldParameter> map(JsonNode node) {
    return controlFieldSettings.stream()
      .filter(setting -> node.has(setting.property))
      .map(setting -> parseControlField(node, setting))
      .toList();
  }

  private ControlFieldParameter parseControlField(JsonNode node, ControlFieldSettings setting) {
    var property = setting.property;
    var propertyValue = node.get(property).get(0).asText();
    propertyValue = dictionaryProcessor.getKey(setting.key(), propertyValue)
      .orElse(propertyValue);
    return new ControlFieldParameter(tag, propertyValue, setting.start, setting.end);
  }

  private record ControlFieldSettings(String key, String property, int start, int end) {
  }
}
