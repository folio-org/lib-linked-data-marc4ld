package org.folio.marc4ld.service.ld2marc.condition;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
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

  @Override
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
      .allMatch(entry -> checkEdgeRule(resource, entry));
  }

  private boolean checkEdgeRule(Resource resource,
                                Map.Entry<String, Marc4LdRules.Ld2marcEdgeMatchCondition> predicateAndCondition) {
    var edges = getOutgoingEdges(resource, PredicateDictionary.valueOf(predicateAndCondition.getKey()))
      .stream()
      .map(ResourceEdge::getTarget)
      .toList();
    var matchCondition = predicateAndCondition.getValue();

    if (nonNull(matchCondition.getAnyMatch())) {
      return matchCondition.getAnyMatch().stream().anyMatch(c -> checkEdgeRule(c, edges));
    }
    if (nonNull(matchCondition.getAllMatch())) {
      return matchCondition.getAllMatch().stream().allMatch(c -> checkEdgeRule(c, edges));
    }
    return false;
  }


  private boolean checkEdgeRule(Marc4LdRules.Ld2marcEdgeCondition condition, List<Resource> edgeResources) {
    if (edgeResources.isEmpty()) {
      return !condition.isPresent();
    }
    if (nonNull(condition.getProperties())) {
      return edgeResources.stream().anyMatch(r -> resourceHasAllProperties(r, condition.getProperties()));
    }
    return condition.isPresent();
  }

  private boolean resourceHasAllProperties(Resource resource, Map<String, String> propsAndValues) {
    return propsAndValues.entrySet().stream().allMatch(
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

