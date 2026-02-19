package org.folio.marc4ld.service.label.processor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Legacy fallback processor that generates a UUID label when no configured label can be produced.
 *
 * @deprecated use {@code org.folio.ld.dictionary.label.LabelGeneratorService} for label generation.
 */
@Deprecated(forRemoval = true)
public class UuidLabelProcessor implements LabelProcessor {

  @Override
  public String apply(Map<String, List<String>> properties) {
    return UUID.randomUUID().toString();
  }
}
