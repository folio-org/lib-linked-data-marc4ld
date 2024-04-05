package org.folio.marc4ld.service.marc2ld.field.property.merger.function;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConcatinatedMergeFunction implements MergeFunction {

  private final String concat;

  @Override
  public List<String> apply(List<String> values, List<String> newValues) {
    var concatenated = values.get(0).concat(concat).concat(newValues.get(0));
    values.set(0, concatenated);
    return values;
  }
}
