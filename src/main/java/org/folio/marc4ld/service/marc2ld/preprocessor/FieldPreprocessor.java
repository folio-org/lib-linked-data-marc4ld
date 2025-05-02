package org.folio.marc4ld.service.marc2ld.preprocessor;

import java.util.List;
import java.util.function.Function;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor.PreprocessorContext;
import org.marc4j.marc.DataField;


public interface FieldPreprocessor extends Function<PreprocessorContext, List<DataField>> {
}
