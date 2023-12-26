package org.folio.marc4ld.service.ld2marc.resource.field;

import static org.apache.commons.lang3.StringUtils.SPACE;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.MarcFactory;

@RequiredArgsConstructor
public class ControlFieldsBuilder {
  private static final String EMPTY_CF = SPACE.repeat(39);
  private final Map<String, StringBuilder> storage = new LinkedHashMap<>();

  public void addFieldValue(String tag, String newValue, Integer startPos, Integer endPos) {
    storage.putIfAbsent(tag, new StringBuilder(EMPTY_CF));
    storage.get(tag).replace(startPos - 1, endPos - 1, newValue);
  }

  public Stream<ControlField> build(MarcFactory factory) {
    return storage.entrySet().stream().map(e -> factory.newControlField(e.getKey(), e.getValue().toString()));
  }
}
