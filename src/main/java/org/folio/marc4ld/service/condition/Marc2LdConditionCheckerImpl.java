package org.folio.marc4ld.service.condition;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Service;

@Service
public class Marc2LdConditionCheckerImpl implements Marc2LdConditionChecker {

  public static final String NOT = "!";
  public static final String PRESENTED = "presented";
  public static final String NOT_PRESENTED = "not_presented";

  public boolean isMarc2LdConditionSatisfied(Marc4LdRules.FieldRule fieldRule, DataField dataField, Record marcRecord) {
    var condition = fieldRule.getMarc2ldCondition();
    if (isNull(condition)) {
      return true;
    }
    return !condition.isSkip()
      && isInd1Condition(dataField, condition)
      && isInd2Condition(dataField, condition)
      && isControlFieldConditions(marcRecord.getControlFields(), condition)
      && isLeaderConditions(marcRecord.getLeader(), condition)
      && isAllOfFieldConditions(dataField, condition)
      && isFieldAnyOfConditions(dataField, condition);
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

  private boolean isControlFieldConditions(List<ControlField> controlFields,
                                           Marc4LdRules.Marc2ldCondition condition) {
    return isEmpty(condition.getControlFields()) || condition.getControlFields().stream()
      .flatMap(cfc -> controlFields.stream()
        .filter(cf -> cf.getTag().equals(cfc.getTag()))
        .map(cf -> evaluateControlField(cfc, cf)))
      .allMatch(b -> b);
  }

  private Boolean evaluateControlField(Marc4LdRules.ControlFieldContext cfc, ControlField cf) {
    var data = substring(cf.getData(), cfc.getSubstring());
    if (!isAnyMatch(data, cfc.getIsAny())) {
      return false;
    }
    var isBlank = cfc.getIsBlank();
    return isBlank == null || isBlank.booleanValue() == data.isBlank();
  }

  private boolean isLeaderConditions(Leader leader, Marc4LdRules.Marc2ldCondition condition) {
    return isEmpty(condition.getLeader()) || condition.getLeader()
      .stream()
      .allMatch(leaderCondition -> evaluateLeaderValue(leaderCondition, leader.marshal()));
  }

  private Boolean evaluateLeaderValue(Marc4LdRules.Leader leaderCondition, String leader) {
    var data = substring(leader, leaderCondition.getSubstring());
    return isAnyMatch(data, leaderCondition.getIsAny());
  }

  private static boolean isAnyMatch(String data, List<String> isAny) {
    return isEmpty(isAny) || isAny.contains(data);
  }

  static String substring(String data, List<Integer> substring) {
    if (isEmpty(substring)) {
      return data;
    }
    int start = substring.getFirst();
    if (start >= data.length()) {
      return "";
    }
    int end = substring.getLast();
    if (end > data.length()) {
      return data.substring(start);
    }
    return data.substring(start, end);
  }
}
