package org.folio.marc4ld.service.ld2marc.mapper.controlfield;

import static java.time.Instant.ofEpochMilli;
import static java.util.Optional.ofNullable;
import static org.folio.ld.dictionary.PropertyDictionary.CREATED_DATE;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.LdUtil.getAdminMetadata;
import static org.folio.marc4ld.util.LdUtil.isInstance;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.mapper.CustomControlFieldsMapper;
import org.folio.marc4ld.service.ld2marc.resource.field.ControlFieldsBuilder;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class CreatedDateMapper implements CustomControlFieldsMapper {

  private static final DateTimeFormatter MARC_CREATED_DATE_FORMAT = DateTimeFormatter
    .ofPattern("yyMMdd")
    .withZone(ZoneOffset.UTC);

  @Override
  public void map(Resource resource, ControlFieldsBuilder controlFieldsBuilder) {
    if (isInstance(resource)) {
      var optionalCreatedDate = getCreatedDateFromResource(resource);
      optionalCreatedDate.ifPresent(
        date -> controlFieldsBuilder.addFieldValue(TAG_008, conditionalReformatDate(date), 0, 6));
    }
  }

  private Optional<String> getCreatedDateFromResource(Resource resource) {
    return getAdminMetadata(resource)
      .map(Resource::getDoc)
      .map(doc -> doc.get(CREATED_DATE.getValue()))
      .filter(JsonNode::isArray)
      .map(value -> value.get(0))
      .filter(node -> !node.isNull())
      .map(JsonNode::asString)
      .or(() -> ofNullable(resource.getCreatedDate())
        .map(date -> MARC_CREATED_DATE_FORMAT.format(ofEpochMilli(date.getTime()))));
  }

  private String conditionalReformatDate(String dateFromResource) {
    var formatted = dateFromResource;
    if (dateFromResource.length() > 6) {
      var localDate = LocalDate.parse(dateFromResource);
      formatted = MARC_CREATED_DATE_FORMAT.format(localDate);
    }
    return formatted;
  }
}
