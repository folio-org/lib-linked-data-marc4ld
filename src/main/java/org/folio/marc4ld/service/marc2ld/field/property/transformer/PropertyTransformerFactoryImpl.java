package org.folio.marc4ld.service.marc2ld.field.property.transformer;

import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PropertyTransformerFactoryImpl implements PropertyTransformerFactory {

  private final SeparatePropertyTransformer separatePropertyTransformer;
  private final DefaultPropertyTransformer defaultPropertyTransformer;

  @Override
  public PropertyTransformer get(Marc4BibframeRules.FieldRule rule) {
    if (rule.isMultiply()) {
      validateMultiply(rule);
      return separatePropertyTransformer;
    }
    return defaultPropertyTransformer;
  }

  private void validateMultiply(Marc4BibframeRules.FieldRule rule) {
    if (rule.getSubfields().size() != 1) {
      throw new IllegalArgumentException("Only 1 subfield is required for multiply rule");
    }
  }
}
