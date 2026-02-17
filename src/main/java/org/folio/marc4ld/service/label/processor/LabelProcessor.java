package org.folio.marc4ld.service.label.processor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Legacy label processor contract used by the deprecated label generation fallback path.
 *
 * @deprecated use {@code org.folio.ld.dictionary.label.LabelGeneratorService} for label generation.
 */
@Deprecated
public interface LabelProcessor extends Function<Map<String, List<String>>, String> {
}
