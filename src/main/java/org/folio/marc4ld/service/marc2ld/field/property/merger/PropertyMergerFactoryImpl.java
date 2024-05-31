package org.folio.marc4ld.service.marc2ld.field.property.merger;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.service.marc2ld.field.property.merger.function.ConcatinatedMergeFunction;
import org.folio.marc4ld.service.marc2ld.field.property.merger.function.MergeFunction;
import org.folio.marc4ld.service.marc2ld.field.property.merger.function.UnionMergeFunction;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PropertyMergerFactoryImpl implements PropertyMergerFactory {

  private final ConstantPropertyMerger constantPropertyMerger;
  private final UnionMergeFunction unionMergeFunction;

  @Override
  public PropertyMerger getConstant(Marc4BibframeRules.FieldRule rule) {
    return constantPropertyMerger;
  }

  @Override
  public PropertyMerger get(Marc4BibframeRules.FieldRule rule) {
    var mergeFunction = getMergeFunction(rule);
    return new PropertyMergerImpl(mergeFunction);
  }

  private MergeFunction getMergeFunction(Marc4BibframeRules.FieldRule rule) {
    return Optional.ofNullable(rule.getConcat())
      .map(ConcatinatedMergeFunction::new)
      .map(MergeFunction.class::cast)
      .orElse(unionMergeFunction);
  }
}
