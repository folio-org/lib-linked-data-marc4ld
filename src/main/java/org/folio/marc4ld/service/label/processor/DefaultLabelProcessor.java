package org.folio.marc4ld.service.label.processor;

import java.util.List;
import java.util.Map;

/**
 * Legacy fallback processor that generates a UUID label when no configured label can be produced.
 *
 * @deprecated use {@code org.folio.ld.dictionary.label.LabelGeneratorService} for label generation.
 */
@Deprecated(forRemoval = true)
public class DefaultLabelProcessor implements LabelProcessor {

  @Override
  public String apply(Map<String, List<String>> properties) {
    return "";
  }
}
