package org.folio.marc4ld.service.marc2ld.label;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface LabelProcessor extends Function<Map<String, List<String>>, String> {
}
