package org.folio.marc4ld.service.ld2marc.resource.field;

import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.rightPad;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.MarcFactory;

public class ControlFieldsBuilder {
  private static final String EMPTY_CF = SPACE.repeat(39);
  private final Map<String, StringBuilder> storage = new LinkedHashMap<>();

  public void addFieldValue(String tag, String newValue, Integer startPos, Integer endPos) {
    if (isNotEmpty(newValue)) {
      storage.putIfAbsent(tag, new StringBuilder(EMPTY_CF));
      storage.get(tag).replace(startPos, endPos, ensureLength(newValue, endPos - startPos));
    }
  }

  public Stream<ControlField> build(MarcFactory factory) {
    return storage.entrySet().stream().map(e -> factory.newControlField(e.getKey(), e.getValue().toString()));
  }

  private String ensureLength(String str, int size) {
    str = str.strip();
    if (str.length() > size) {
      return str.substring(0, size);
    } else {
      return rightPad(str, size);
    }
  }
}
