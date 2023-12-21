package org.folio.marc4ld.service.condition;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.model.Resource;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Service;

@Service
public class ConditionCheckerImpl implements ConditionChecker {

  public static final String NOT = "!";
  public static final String PRESENTED = "presented";

  @Override
  public boolean isConditionSatisfied(Marc4BibframeRules.FieldRule fieldRule, DataField dataField) {
    var condition = fieldRule.getCondition();
    if (isNull(condition)) {
      return true;
    }
    var ind1Condition = isSingleConditionSatisfied(String.valueOf(dataField.getIndicator1()), condition.getInd1());
    var ind2Condition = isSingleConditionSatisfied(String.valueOf(dataField.getIndicator2()), condition.getInd2());
    var fieldConditions = isEmpty(condition.getFields()) || condition.getFields().entrySet().stream()
      .allMatch(fieldCondition -> ofNullable(dataField.getSubfield(fieldCondition.getKey()))
        .map(sf -> isSingleConditionSatisfied(sf.getData(), fieldCondition.getValue()))
        .orElse(false));
    return ind1Condition && ind2Condition && fieldConditions;
  }

  @Override
  public boolean isResourceConditionSatisfied(Marc4BibframeRules.FieldRule fieldRule, Resource resource) {
    var condition = fieldRule.getCondition();
    if (isNull(condition) || isNull(fieldRule.getCondition().getEdge())) {
      return true;
    }
    return isEdgeConditionSatisfied(fieldRule, resource);
  }

  private boolean isSingleConditionSatisfied(String value, String condition) {
    if (StringUtils.isEmpty(condition)) {
      return true;
    }
    if (condition.contains(NOT)) {
      condition = condition.replace(NOT, EMPTY);
      return !Objects.equals(value, condition);
    }
    if (condition.contains(PRESENTED)) {
      return isNotEmpty(value);
    }
    return Objects.equals(value, condition);
  }

  private boolean isEdgeConditionSatisfied(Marc4BibframeRules.FieldRule fieldRule, Resource resource) {
    if (isEmpty(resource.getOutgoingEdges())) {
      return false;
    }
    return fieldRule.getEdges().stream()
      .filter(edgeRule -> edgeRule.getTypes().contains(fieldRule.getCondition().getEdge()))
      .filter(edgeRule -> nonNull(edgeRule.getPredicate()))
      .findFirst()
      .map(edgeRule -> resource.getOutgoingEdges().stream()
        .filter(re -> re.getPredicate().equals(PredicateDictionary.valueOf(edgeRule.getPredicate())))
        .anyMatch(re -> isResourceSatisfiesRule(re.getTarget(), edgeRule)))
      .orElse(false);
  }

  private boolean isResourceSatisfiesRule(Resource resource, Marc4BibframeRules.FieldRule rule) {
    var types = rule.getTypes().stream().map(ResourceTypeDictionary::valueOf).collect(toSet());
    var constantsPresented = isConstantsPresented(rule.getConstants(), resource.getDoc());
    return Objects.equals(resource.getTypes(), types) && constantsPresented;
  }

  private boolean isConstantsPresented(Map<String, String> constants, JsonNode doc) {
    return constants.entrySet().stream().allMatch(e -> {
      var property = PropertyDictionary.valueOf(e.getKey()).getValue();
      return nonNull(doc) && doc.has(property) && !doc.get(property).isEmpty()
        && e.getValue().equals(doc.get(property).get(0).asText());
    });
  }

}
