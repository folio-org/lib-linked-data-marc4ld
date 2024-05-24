package org.folio.marc4ld.service.marc2ld.label;

import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.SPACE;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SinglePropertyLabelProcessor implements LabelProcessor {

  private final String property;
  private final LabelProcessor defaultProcessor;

  @Override
  public String apply(Map<String, List<String>> properties) {
    return Optional.of(property)
      .map(properties::get)
      .map(vs -> join(SPACE, vs))
      .orElseGet(() -> defaultProcessor.apply(properties));
  }
}
