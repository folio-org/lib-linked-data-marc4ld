package org.folio.marc4ld.configuration.property;

import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class Marc4LdRulesPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof Marc4LdRules marc4LdRules) {
      postProcess(marc4LdRules);
      return bean;
    }
    return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
  }

  private void postProcess(Marc4LdRules marc4LdRules) {
    processRules(marc4LdRules.getBibFieldRules(), marc4LdRules.getBibSharedRules());
    processRules(marc4LdRules.getAuthorityFieldRules(), marc4LdRules.getSharedAuthorityRules());
  }

  private void processRules(Map<String, List<Marc4LdRules.FieldRule>> fieldRules,
                            Map<String, Marc4LdRules.FieldRule> sharedRules) {
    fieldRules.values()
      .stream()
      .flatMap(Collection::stream)
      .forEach(rule -> copyIncludedRuleRecursive(sharedRules, rule));
  }

  private void copyIncludedRuleRecursive(Map<String, Marc4LdRules.FieldRule> sharedRules, Marc4LdRules.FieldRule rule) {
    if (rule.getInclude() != null) {
      var includedRule = sharedRules.get(rule.getInclude());
      copyRule(includedRule, rule);
    }

    if (isNotEmpty(rule.getEdges())) {
      rule.getEdges()
        .forEach(edgeRule -> copyIncludedRuleRecursive(sharedRules, edgeRule));
    }
  }

  private void copyRule(Marc4LdRules.FieldRule source, Marc4LdRules.FieldRule target) {
    setIfNull(target::getParent, source::getParent, target::setParent);
    setIfNull(target::getPredicate, source::getPredicate, target::setPredicate);
    setIfNull(target::getMarc2ldCondition, source::getMarc2ldCondition, target::setMarc2ldCondition);
    setIfNull(target::getLd2marcCondition, source::getLd2marcCondition, target::setLd2marcCondition);
    setIfNull(target::getRelation, source::getRelation, target::setRelation);
    setIfNull(target::getInd1, source::getInd1, target::setInd1);
    setIfNull(target::getInd2, source::getInd2, target::setInd2);
    setIfNull(target::getConcat, source::getConcat, target::setConcat);

    ofNullable(source.getTypes()).ifPresent(target::addTypes);
    ofNullable(source.getSubfields()).ifPresent(target::putSubfields);
    ofNullable(source.getConstants()).ifPresent(target::putConstants);
    ofNullable(source.getControlFields()).ifPresent(target::putControlFields);
    ofNullable(source.getEdges()).ifPresent(target::addEdges);

    target.setAppend(source.isAppend());
    target.setMultiply(source.isMultiply());
    target.setIncludeMarcKey(source.isIncludeMarcKey());
  }

  private <T> void setIfNull(Supplier<T> targetGetter, Supplier<T> sourceGetter, Consumer<T> targetSetter) {
    T targetValue = targetGetter.get();
    T sourceValue = sourceGetter.get();
    if (targetValue == null && sourceValue != null) {
      targetSetter.accept(sourceValue);
    }
  }
}
