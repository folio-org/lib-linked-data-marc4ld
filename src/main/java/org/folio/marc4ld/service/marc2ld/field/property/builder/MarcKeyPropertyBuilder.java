package org.folio.marc4ld.service.marc2ld.field.property.builder;

import static org.folio.ld.dictionary.PropertyDictionary.MARC_KEY;
import static org.folio.marc4ld.util.JsonUtil.getJsonMapper;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.folio.marc4ld.service.marc2ld.field.property.Property;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectWriter;

@Component
@Log4j2
public class MarcKeyPropertyBuilder implements PropertyBuilder<DataField> {

  private final ObjectWriter objectWriter;

  public MarcKeyPropertyBuilder() {
    this.objectWriter = getJsonMapper()
      .writer()
      .without(INDENT_OUTPUT);
  }

  @Override
  public Collection<Property> apply(DataField dataField) {
    var subfields = dataField.getSubfields().stream()
      .map(subfield -> Map.entry(subfield.getCode(), subfield.getData()))
      .toList();
    var fieldContent = new DataFieldJson(subfields, dataField.getIndicator1(), dataField.getIndicator2());
    return toJsonString(dataField.getTag(), fieldContent)
      .map(json -> List.of(new Property(MARC_KEY.name(), json)))
      .orElse(List.of());
  }

  private Optional<String> toJsonString(String tag, DataFieldJson dataField) {
    try {
      return Optional.of(objectWriter.writeValueAsString(Map.of(tag, dataField)));
    } catch (JacksonException exception) {
      log.error("Failed to serialize marc. {}: {}", tag, dataField, exception);
      return Optional.empty();
    }
  }

  record DataFieldJson(List<Map.Entry<Character, String>> subfields, char ind1, char ind2) {
  }
}
