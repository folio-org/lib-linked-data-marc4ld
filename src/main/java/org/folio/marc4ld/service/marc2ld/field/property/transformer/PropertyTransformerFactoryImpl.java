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
    if (rule.getTypes().contains("PLACE")) {
      return separatePropertyTransformer;
    }
    return defaultPropertyTransformer;
  }
}
