package org.folio.marc2ld.mapper.condition;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.Objects;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Service;

@Service
public class ConditionCheckerImpl implements ConditionChecker {

  private static final String NOT = "!";
  private static final String PRESENTED = "presented";

  @Override
  public boolean isConditionSatisfied(Marc2BibframeRules.FieldRule fieldRule, DataField dataField) {
    var condition = fieldRule.getCondition();
    if (isNull(condition)) {
      return true;
    }
    boolean ind1Condition = isSingleConditionSatisfied(String.valueOf(dataField.getIndicator1()), condition.getInd1());
    boolean ind2Condition = isSingleConditionSatisfied(String.valueOf(dataField.getIndicator2()), condition.getInd2());
    boolean fieldConditions = condition.getFields().entrySet().stream()
      .allMatch(fieldCondition -> ofNullable(dataField.getSubfield(fieldCondition.getKey()))
        .map(sf -> isSingleConditionSatisfied(sf.getData(), fieldCondition.getValue()))
        .orElse(false));
    return ind1Condition && ind2Condition && fieldConditions;
  }

  private boolean isSingleConditionSatisfied(String value, String condition) {
    if (isEmpty(condition)) {
      return true;
    }
    if (condition.contains(NOT)) {
      condition = condition.replace(NOT, "");
      return !Objects.equals(value, condition);
    }
    if (condition.contains(PRESENTED)) {
      return isNotEmpty(value);
    }
    return Objects.equals(value, condition);
  }
}