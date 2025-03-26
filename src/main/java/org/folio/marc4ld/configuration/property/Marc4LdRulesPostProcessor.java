package org.folio.marc4ld.configuration.property;

import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
      .forEach(rule -> Optional.of(rule)
        .map(Marc4LdRules.FieldRule::getInclude)
        .map(sharedRules::get)
        .ifPresent(sharedRule -> copyRule(sharedRule, rule))
      );
  }

  private void copyRule(Marc4LdRules.FieldRule source, Marc4LdRules.FieldRule target) {
    ofNullable(source.getTypes()).ifPresent(target::addTypes);
    ofNullable(source.getParent()).ifPresent(target::setParent);
    ofNullable(source.getPredicate()).ifPresent(target::setPredicate);
    ofNullable(source.getMarc2ldCondition()).ifPresent(target::setMarc2ldCondition);
    ofNullable(source.getLd2marcCondition()).ifPresent(target::setLd2marcCondition);
    ofNullable(source.getRelation()).ifPresent(target::setRelation);
    ofNullable(source.getSubfields()).ifPresent(target::putSubfields);
    ofNullable(source.getInd1()).ifPresent(target::setInd1);
    ofNullable(source.getInd2()).ifPresent(target::setInd2);
    ofNullable(source.getConcat()).ifPresent(target::setConcat);
    ofNullable(source.getConstants()).ifPresent(target::putConstants);
    ofNullable(source.getControlFields()).ifPresent(target::putControlFields);
    ofNullable(source.getEdges()).ifPresent(target::addEdges);

    target.setAppend(source.isAppend());
    target.setMultiply(source.isMultiply());
  }
}
