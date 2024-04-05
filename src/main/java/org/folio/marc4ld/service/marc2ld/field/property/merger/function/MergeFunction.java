package org.folio.marc4ld.service.marc2ld.field.property.merger.function;

import java.util.List;
import java.util.function.BiFunction;

public interface MergeFunction extends BiFunction<List<String>, List<String>, List<String>> {

}
