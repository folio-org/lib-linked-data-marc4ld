package org.folio.marc4ld.configuration.property;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class Marc4BibframeRulesPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof Marc4BibframeRules marc4BibframeRules) {
      postProcess(marc4BibframeRules);
      return bean;
    }
    return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
  }

  private void postProcess(Marc4BibframeRules marc4BibframeRules) {
    marc4BibframeRules.getFieldRules().forEach((tag, rules) ->
      rules.forEach(rule -> {
        var include = rule.getInclude();
        if (include != null) {
          copyRule(marc4BibframeRules.getSharedRules().get(include), rule);
        }
      })
    );
  }

  private void copyRule(Marc4BibframeRules.FieldRule source, Marc4BibframeRules.FieldRule target) {
    var types = source.getTypes();
    if (types != null) {
      target.setTypes(types);
    }

    var parent = source.getParent();
    if (parent != null) {
      target.setParent(parent);
    }

    var parentPredicate = source.getParentPredicate();
    if (parentPredicate != null) {
      target.setParentPredicate(parentPredicate);
    }

    var predicate = source.getPredicate();
    if (predicate != null) {
      target.setPredicate(predicate);
    }

    var marc2ldCondition = source.getMarc2ldCondition();
    if (marc2ldCondition != null) {
      target.setMarc2ldCondition(marc2ldCondition);
    }

    var ld2marcCondition = source.getLd2marcCondition();
    if (ld2marcCondition != null) {
      target.setLd2marcCondition(ld2marcCondition);
    }

    var relation = source.getRelation();
    if (relation != null) {
      target.setRelation(relation);
    }

    var subfields = source.getSubfields();
    if (subfields != null) {
      target.setSubfields(subfields);
    }

    var ind2 = source.getInd2();
    if (ind2 != null) {
      target.setInd2(ind2);
    }

    var label = source.getLabel();
    if (label != null) {
      target.setLabel(label);
    }

    var concat = source.getConcat();
    if (concat != null) {
      target.setConcat(concat);
    }

    var append = source.isAppend();
    if (append) {
      target.setAppend(append);
    }


    var constants = source.getConstants();
    if (constants != null) {
      target.setConstants(constants);
    }

    var controlFields = source.getControlFields();
    if (controlFields != null) {
      target.setControlFields(controlFields);
    }

    var edges = source.getEdges();
    if (edges != null) {
      target.setEdges(edges);
    }
  }
}
