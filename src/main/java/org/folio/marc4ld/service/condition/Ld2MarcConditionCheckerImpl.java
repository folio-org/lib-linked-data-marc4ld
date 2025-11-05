package org.folio.marc4ld.service.condition;

import static java.util.Objects.isNull;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.util.LdUtil.getOutgoingEdges;
import static org.folio.marc4ld.util.LdUtil.getPropertyValues;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.springframework.stereotype.Service;

@Service
public class Ld2MarcConditionCheckerImpl implements Ld2MarcConditionChecker {

  public boolean isLd2MarcConditionSatisfied(Marc4LdRules.FieldRule fieldRule, Resource resource, Resource parent) {
    var condition = fieldRule.getLd2marcCondition();
    if (isNull(condition)) {
      return true;
    }
    if (condition.isSkip()) {
      return false;
    }
    return isEdgeConditionSatisfied(fieldRule, resource)
      && isWorkTypeConditionSatisfied(condition.getWorkType(), parent);
  }

  private boolean isEdgeConditionSatisfied(Marc4LdRules.FieldRule fieldRule, Resource resource) {
    var edgeRules = fieldRule.getLd2marcCondition().getEdge();
    if (isNull(edgeRules)) {
      return true;
    }
    return edgeRules.entrySet().stream()
      .findFirst()
      .map(entry -> checkEdgeRule(resource, entry))
      .orElse(false);
  }

  private Boolean checkEdgeRule(Resource resource,
                                Map.Entry<String, Marc4LdRules.Ld2marcEdgeMatchCondition> predicateAndCondition) {
    var edgePredicate = PredicateDictionary.valueOf(predicateAndCondition.getKey());
    var edges = getOutgoingEdges(resource, edgePredicate)
      .stream()
      .map(ResourceEdge::getTarget)
      .toList();
    var matchCondition = predicateAndCondition.getValue();
    if (matchCondition.getAnyMatch() != null) {
      var conditions = matchCondition.getAnyMatch();
      return conditions.stream().anyMatch(c -> checkEdgeRule(c, edges));
    }
    if (matchCondition.getAllMatch() != null) {
      var conditions = matchCondition.getAllMatch();
      return conditions.stream().allMatch(c -> checkEdgeRule(c, edges));
    }
    return false;
  }

  private boolean checkEdgeRule(Marc4LdRules.Ld2marcEdgeCondition condition, List<Resource> edgeResources) {
    if (condition.getPresent() == Boolean.FALSE && edgeResources.isEmpty()) {
      return true;
    }
    if (condition.getPresent() == Boolean.TRUE && condition.getProperties() != null) {
      return edgeResources.stream().anyMatch(r -> resourceHasAllProperties(r, condition.getProperties()));
    }
    if (condition.getPresent() == Boolean.TRUE) {
      return !edgeResources.isEmpty();
    }
    return false;
  }

  private boolean resourceHasAllProperties(Resource resource, Map<String, String> properties) {
    return properties.entrySet()
      .stream()
      .allMatch(
        propAndValue -> getPropertyValues(resource, PropertyDictionary.valueOf(propAndValue.getKey()).getValue())
          .contains(propAndValue.getValue())
      );
  }

  private boolean isWorkTypeConditionSatisfied(ResourceTypeDictionary conditionalWorkType, Resource parent) {
    if (isNull(conditionalWorkType) || isNull(parent) || !parent.isOfType(WORK)) {
      return true;
    }
    return parent.isOfType(conditionalWorkType);
  }
}

