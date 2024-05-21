package org.folio.marc4ld.service.marc2ld.field.property.merger.function;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SkipAdditionalMergeFunction implements MergeFunction {

  @Override
  public List<String> apply(List<String> values, List<String> newValues) {
    return values;
  }
}
