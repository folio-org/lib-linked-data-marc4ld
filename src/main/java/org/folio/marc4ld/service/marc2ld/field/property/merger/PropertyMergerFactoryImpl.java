package org.folio.marc4ld.service.marc2ld.field.property.merger;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
  private final Map<String, MergeFunction> functions;

  @Autowired
  public PropertyMergerFactoryImpl(
    ConstantPropertyMerger constantPropertyMerger,
    UnionMergeFunction unionMergeFunction,
    SkipAdditionalMergeFunction skipAdditionalMergeFunction
  ) {
    this.constantPropertyMerger = constantPropertyMerger;
    this.unionMergeFunction = unionMergeFunction;
    this.skipAdditionalMergeFunction = skipAdditionalMergeFunction;
    this.functions = new HashMap<>();
    functions.put("GEOGRAPHIC_SUBDIVISION", unionMergeFunction);
    functions.put("GEOGRAPHIC_AREA_CODE", skipAdditionalMergeFunction);
  }

  @Override
  public PropertyMerger getConstant(Marc4BibframeRules.FieldRule rule) {
    return constantPropertyMerger;
  }

  @Override
  public PropertyMerger get(Marc4BibframeRules.FieldRule rule) {
    if (isPlace(rule)) {
      return new PropertyMergerImpl(unionMergeFunction, functions);
    }
    var concat = parseConcat(rule);
    return new PropertyMergerImpl(new ConcatinatedMergeFunction(concat), functions);
  }

  private static boolean isPlace(Marc4BibframeRules.FieldRule rule) {
    return rule.getTypes().contains("PLACE");
  }

  private static String parseConcat(Marc4BibframeRules.FieldRule rule) {
    return Optional.of(rule)
      .map(Marc4BibframeRules.FieldRule::getConcat)
      .orElse(EMPTY);
  }
}
