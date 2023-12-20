package org.folio.marc4ld.mapper.marc2ld.condition;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
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
}
