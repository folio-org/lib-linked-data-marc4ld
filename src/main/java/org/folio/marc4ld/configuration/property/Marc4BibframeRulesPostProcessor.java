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
    processRules(marc4BibframeRules.getBibFieldRules(), marc4BibframeRules.getBibSharedRules());
    processRules(marc4BibframeRules.getAuthorityFieldRules(), marc4BibframeRules.getSharedAuthorityRules());
  }

  private void processRules(Map<String, List<Marc4BibframeRules.FieldRule>> fieldRules,
                            Map<String, Marc4BibframeRules.FieldRule> sharedRules) {
    fieldRules.values()
      .stream()
      .flatMap(Collection::stream)
      .forEach(rule -> Optional.of(rule)
        .map(Marc4BibframeRules.FieldRule::getInclude)
        .map(sharedRules::get)
        .ifPresent(sharedRule -> copyRule(sharedRule, rule))
      );
  }

  private void copyRule(Marc4BibframeRules.FieldRule source, Marc4BibframeRules.FieldRule target) {
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

    if (source.isAppend()) {
      target.setAppend(true);
    }
  }
}
