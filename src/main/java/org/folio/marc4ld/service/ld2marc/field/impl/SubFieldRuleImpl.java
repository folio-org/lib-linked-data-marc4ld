package org.folio.marc4ld.service.ld2marc.field.impl;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.marc4ld.service.ld2marc.field.SubFieldRule;
import org.folio.marc4ld.service.ld2marc.field.param.SubFieldParameter;

public class SubFieldRuleImpl implements SubFieldRule {

  private final char mainKey;
  private final String property;

  public SubFieldRuleImpl(char mainKey, String propertyValue) {
    this.mainKey = mainKey;
    this.property = PropertyDictionary.valueOf(propertyValue).getValue();
  }

  @Override
  public Collection<SubFieldParameter> map(JsonNode node) {
    return Optional.ofNullable(node)
      .map(prop -> node.get(property))
      .filter(n -> !n.isEmpty())
      .map(this::getSubNodes)
      .map(i -> i.stream().map(JsonNode::asText).toList())
      .map(this::getParameters)
      .orElseGet(Collections::emptyList);
  }

  private List<SubFieldParameter> getParameters(List<String> strings) {
    return strings.stream()
      .map(value -> new SubFieldParameter(mainKey, value))
      .toList();
  }

  private List<JsonNode> getSubNodes(JsonNode n) {
    var spliterator = Spliterators.spliteratorUnknownSize(n.iterator(), Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false)
      .toList();
  }
}
