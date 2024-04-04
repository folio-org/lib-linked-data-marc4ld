package org.folio.marc4ld.configuration.property;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Marc4BibframeRulesPostProcessor implements BeanPostProcessor {

  private final ExpressionParser parser;

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof Marc4BibframeRules marc4BibframeRules) {
      postProcess(marc4BibframeRules.getFieldRules());
      return bean;
    }
    return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
  }

  private void postProcess(Map<String, List<Marc4BibframeRules.FieldRule>> fieldRules) {
    fieldRules.forEach((tag, rules) ->
      rules.forEach(rule -> {
        var include = rule.getInclude();
        if (include != null) {
          var source = fieldRules.get(include.getTag()).get(include.getIndex());
          copyRule(source, rule);
          setSubstitutes(rule, include.getSubstitutes());
        }
      })
    );
  }

  private void copyRule(Marc4BibframeRules.FieldRule source, Marc4BibframeRules.FieldRule target) {
    target.setTypes(source.getTypes());
    target.setParent(source.getParent());
    target.setParentPredicate(source.getParentPredicate());
    target.setPredicate(source.getPredicate());
    target.setMarc2ldCondition(source.getMarc2ldCondition());
    target.setLd2marcCondition(source.getLd2marcCondition());
    target.setRelation(source.getRelation());
    target.setSubfields(source.getSubfields());
    target.setInd2(source.getInd2());
    target.setLabel(source.getLabel());
    target.setConcat(source.getConcat());
    target.setAppend(source.isAppend());
    target.setConstants(source.getConstants());
    target.setControlFields(source.getControlFields());
    target.setEdges(source.getEdges());
    target.setMappings(source.getMappings());
  }

  private void setSubstitutes(Marc4BibframeRules.FieldRule rule, Map<String, String> substitutes) {
    var context = new StandardEvaluationContext(rule);
    substitutes.forEach((property, substitute) -> parser.parseExpression(property).setValue(context, substitute));
  }
}
