package org.folio.marc4ld.service.ld2marc.field.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.ld2marc.field.ControlFieldRuleApplier;
import org.folio.marc4ld.service.ld2marc.field.param.ControlFieldParameter;
import tools.jackson.databind.JsonNode;

public class ControlFieldRuleApplierImpl implements ControlFieldRuleApplier {

  private final String tag;
  private final Collection<ControlFieldSettings> controlFieldSettings;
  private final DictionaryProcessor dictionaryProcessor;

  public ControlFieldRuleApplierImpl(String tag, Map<String, List<Integer>> rules, DictionaryProcessor processor) {
    this.tag = tag;
    this.dictionaryProcessor = processor;
    this.controlFieldSettings = rules.entrySet()
      .stream()
      .map(entry -> new ControlFieldSettings(
        entry.getKey(),
        PropertyDictionary.valueOf(entry.getKey()).getValue(),
        entry.getValue().getFirst(),
        entry.getValue().get(1))
      )
      .toList();
  }

  @Override
  public Collection<ControlFieldParameter> map(JsonNode node) {
    return Optional.ofNullable(node)
      .map(this::getParameters)
      .orElseGet(Collections::emptyList);
  }

  private Collection<ControlFieldParameter> getParameters(JsonNode node) {
    return controlFieldSettings.stream()
      .filter(setting -> node.has(setting.property))
      .map(setting -> parseControlField(node, setting))
      .toList();
  }

  private ControlFieldParameter parseControlField(JsonNode node, ControlFieldSettings setting) {
    var property = setting.property;
    var propertyValue = node.get(property).get(0).asString();
    propertyValue = dictionaryProcessor.getKey(setting.key(), propertyValue)
      .orElse(propertyValue);
    return new ControlFieldParameter(tag, propertyValue, setting.start, setting.end);
  }

  private record ControlFieldSettings(String key, String property, int start, int end) {
  }
}
