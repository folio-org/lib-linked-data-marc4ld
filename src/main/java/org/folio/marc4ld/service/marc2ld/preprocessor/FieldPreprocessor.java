package org.folio.marc4ld.service.marc2ld.preprocessor;

import java.util.List;
import java.util.function.Function;
import org.marc4j.marc.DataField;


public interface FieldPreprocessor extends Function<DataFieldPreprocessor.PreprocessorContext, List<DataField>> {
}
