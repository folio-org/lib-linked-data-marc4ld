package org.folio.marc4ld.service.condition;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Log4j2
public class ConditionCheckerImpl implements ConditionChecker {

  public static final String NOT = "!";
  public static final String PRESENTED = "presented";
  public static final String NOT_PRESENTED = "not_presented";

  private final ExpressionParser expressionParser;

  @Override
  public boolean isMarc2LdConditionSatisfied(Marc4LdRules.FieldRule fieldRule, DataField dataField,
                                             List<ControlField> controlFields) {
    var condition = fieldRule.getMarc2ldCondition();
    if (isNull(condition)) {
      return true;
    }
    return
      !condition.isSkip()
      && isInd1Condition(dataField, condition)
      && isInd2Condition(dataField, condition)
      && isControlFieldConditions(controlFields, condition)
      && isAllOfFieldConditions(dataField, condition)
      && isFieldAnyOfConditions(dataField, condition);
  }

  @Override
  public boolean isLd2MarcConditionSatisfied(Marc4LdRules.FieldRule fieldRule, Resource resource) {
    var condition = fieldRule.getLd2marcCondition();
    if (isNull(condition)) {
      return true;
    }
    if (condition.isSkip()) {
      return false;
    }
    return isNull(condition.getEdge()) || isEdgeConditionSatisfied(fieldRule, resource);
  }

  private boolean isAllOfFieldConditions(DataField dataField, Marc4LdRules.Marc2ldCondition condition) {
    return isEmpty(condition.getFieldsAllOf())
      || condition.getFieldsAllOf().entrySet()
      .stream()
      .allMatch(fieldCondition -> isValueSatisfied(dataField, fieldCondition));
  }

  private boolean isFieldAnyOfConditions(DataField dataField, Marc4LdRules.Marc2ldCondition condition) {
    return isEmpty(condition.getFieldsAnyOf())
      || condition.getFieldsAnyOf().entrySet()
      .stream()
      .anyMatch(fieldCondition -> isValueSatisfied(dataField, fieldCondition));
  }

  private boolean isValueSatisfied(DataField dataField, Map.Entry<Character, String> fieldCondition) {
    var value = ofNullable(dataField.getSubfield(fieldCondition.getKey()))
      .map(Subfield::getData)
      .orElse(EMPTY);
    return isSingleConditionSatisfied(value, fieldCondition.getValue());
  }

  private boolean isInd2Condition(DataField dataField, Marc4LdRules.Marc2ldCondition condition) {
    return isSingleConditionSatisfied(String.valueOf(dataField.getIndicator2()), condition.getInd2());
  }

  private boolean isInd1Condition(DataField dataField, Marc4LdRules.Marc2ldCondition condition) {
    return isSingleConditionSatisfied(String.valueOf(dataField.getIndicator1()), condition.getInd1());
  }

  private boolean isSingleConditionSatisfied(String value, String condition) {
    if (StringUtils.isEmpty(condition)) {
      return true;
    }
    if (condition.contains(NOT)) {
      condition = condition.replace(NOT, EMPTY);
      return !Objects.equals(value, condition);
    }
    if (condition.equals(PRESENTED)) {
      return isNotEmpty(value);
    }
    if (condition.equals(NOT_PRESENTED)) {
      return StringUtils.isEmpty(value);
    }
    return Objects.equals(value, condition);
  }

  private boolean isEdgeConditionSatisfied(Marc4LdRules.FieldRule fieldRule, Resource resource) {
    if (isEmpty(resource.getOutgoingEdges())) {
      return false;
    }
    return fieldRule.getEdges().stream()
      .filter(edgeRule -> edgeRule.getTypes().contains(fieldRule.getLd2marcCondition().getEdge()))
      .filter(edgeRule -> nonNull(edgeRule.getPredicate()))
      .findFirst()
      .map(edgeRule -> resource.getOutgoingEdges().stream()
        .filter(re -> re.getPredicate().equals(PredicateDictionary.valueOf(edgeRule.getPredicate())))
        .anyMatch(re -> isResourceSatisfiesRule(re.getTarget(), edgeRule)))
      .orElse(false);
  }

  private boolean isResourceSatisfiesRule(Resource resource, Marc4LdRules.FieldRule rule) {
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

  private boolean isControlFieldConditions(List<ControlField> controlFields,
                                           Marc4LdRules.Marc2ldCondition condition) {
    return isEmpty(condition.getControlFields()) || condition.getControlFields().stream()
      .flatMap(cfc -> controlFields.stream()
        .filter(cf -> cf.getTag().equals(cfc.getTag()))
        .map(cf -> evaluateControlField(cfc, cf)))
      .allMatch(b -> b);
  }

  private Boolean evaluateControlField(Marc4LdRules.ControlFieldContext cfc, ControlField cf) {
    var expression = expressionParser.parseExpression(cfc.getExpression());
    cfc.setData(cf.getData());
    try {
      return expression.getValue(new StandardEvaluationContext(cfc), Boolean.class);
    } catch (Exception e) {
      log.warn("Failed to evaluate control field. Tag: [{}]. Data: [{}]. Expression: [{}]. Args: {}",
        cfc.getTag(), cfc.getData(), cfc.getExpression(), cfc.getArgs(), e);
      return false;
    }
  }

}
