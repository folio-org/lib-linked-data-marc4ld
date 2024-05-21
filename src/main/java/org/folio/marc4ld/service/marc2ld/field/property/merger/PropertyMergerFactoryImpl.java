package org.folio.marc4ld.service.marc2ld.field.property.merger;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.service.marc2ld.field.property.merger.function.ConcatinatedMergeFunction;
import org.folio.marc4ld.service.marc2ld.field.property.merger.function.MergeFunction;
import org.folio.marc4ld.service.marc2ld.field.property.merger.function.SkipAdditionalMergeFunction;
import org.folio.marc4ld.service.marc2ld.field.property.merger.function.UnionMergeFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropertyMergerFactoryImpl implements PropertyMergerFactory {

  private final ConstantPropertyMerger constantPropertyMerger;
  private final UnionMergeFunction unionMergeFunction;
  private final SkipAdditionalMergeFunction skipAdditionalMergeFunction;

  @Autowired
  public PropertyMergerFactoryImpl(ConstantPropertyMerger constantPropertyMerger,
                                   UnionMergeFunction unionMergeFunction,
                                   SkipAdditionalMergeFunction skipAdditionalMergeFunction) {
    this.constantPropertyMerger = constantPropertyMerger;
    this.unionMergeFunction = unionMergeFunction;
    this.skipAdditionalMergeFunction = skipAdditionalMergeFunction;
  }

  @Override
  public PropertyMerger getConstant(Marc4BibframeRules.FieldRule rule) {
    return constantPropertyMerger;
  }

  @Override
  public PropertyMerger get(Marc4BibframeRules.FieldRule rule) {
    var defaultMergeFunction = getMergeFunction(rule);
    var additionalMergeFunctions = getAdditionalMergeFunctions(rule);
    return new PropertyMergerImpl(defaultMergeFunction, additionalMergeFunctions);
  }

  private MergeFunction getMergeFunction(Marc4BibframeRules.FieldRule rule) {
    if (rule.isMultiply()) {
      return unionMergeFunction;
    }
    return Optional.ofNullable(rule.getConcat())
      .map(ConcatinatedMergeFunction::new)
      .map(MergeFunction.class::cast)
      .orElse(skipAdditionalMergeFunction);
  }

  private Map<String, MergeFunction> getAdditionalMergeFunctions(Marc4BibframeRules.FieldRule rule) {
    if (Objects.isNull(rule.getEdges())) {
      return Collections.emptyMap();
    }
    return rule.getEdges()
      .stream()
      .filter(Marc4BibframeRules.FieldRule::isMultiply)
      .map(Marc4BibframeRules.FieldRule::getSubfields)
      .map(Map::keySet)
      .flatMap(Collection::stream)
      .distinct()
      .map(field -> rule.getSubfields().get(field))
      .collect(Collectors.toMap(fieldValue -> fieldValue, entry -> unionMergeFunction));
  }
}
