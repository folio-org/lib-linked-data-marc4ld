package org.folio.marc4ld.service.ld2marc.mapper.custom.impl;

import static java.time.Instant.ofEpochMilli;
import static java.util.Optional.ofNullable;
import static org.folio.ld.dictionary.PropertyDictionary.CREATED_DATE;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.LdUtil.getAdminMetadata;
import static org.folio.marc4ld.util.LdUtil.isInstance;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.mapper.custom.Ld2MarcCustomMapper;
import org.springframework.stereotype.Component;

@Component
public class Ld2MarcCreatedDateMapper implements Ld2MarcCustomMapper {

  private static final DateTimeFormatter MARC_CREATED_DATE_FORMAT = DateTimeFormatter
    .ofPattern("yyMMdd")
    .withZone(ZoneOffset.UTC);

  @Override
  public void map(Resource resource, Context context) {
    if (isInstance(resource)) {
      var optionalCreatedDate = getCreatedDateFromResource(resource);
      optionalCreatedDate.ifPresent(
        date -> context.controlFieldsBuilder().addFieldValue(TAG_008, date, 0, 6));
    }
  }

  private Optional<String> getCreatedDateFromResource(Resource resource) {
    return getAdminMetadata(resource)
      .map(Resource::getDoc)
      .map(doc -> doc.get(CREATED_DATE.getValue()))
      .filter(JsonNode::isArray)
      .map(value -> value.get(0))
      .filter(node -> !node.isNull())
      .map(JsonNode::textValue)
      .or(() -> ofNullable(resource.getCreatedAt())
        .map(date -> MARC_CREATED_DATE_FORMAT.format(ofEpochMilli(date.getTime()))));
  }
}
