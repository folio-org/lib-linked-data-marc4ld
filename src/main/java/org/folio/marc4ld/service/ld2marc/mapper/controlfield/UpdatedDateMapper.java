package org.folio.marc4ld.service.ld2marc.mapper.controlfield;

import static java.time.Instant.ofEpochMilli;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.anyNull;
import static org.folio.marc4ld.util.Constants.TAG_005;
import static org.folio.marc4ld.util.LdUtil.getWork;
import static org.folio.marc4ld.util.LdUtil.isInstance;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.mapper.CustomControlFieldsMapper;
import org.folio.marc4ld.service.ld2marc.resource.field.ControlFieldsBuilder;
import org.springframework.stereotype.Component;

@Component
public class UpdatedDateMapper implements CustomControlFieldsMapper {

  private static final DateTimeFormatter MARC_UPDATED_DATE_FORMAT = DateTimeFormatter
    .ofPattern("yyyyMMddHHmmss.0")
    .withZone(ZoneOffset.UTC);

  @Override
  public void map(Resource resource, ControlFieldsBuilder controlFieldsBuilder) {
    if (isInstance(resource)) {
      var optionalWork = getWork(resource);
      optionalWork.ifPresentOrElse(work -> {
          var optionalDate = chooseDate(resource.getUpdatedDate(), work.getUpdatedDate());
          optionalDate.ifPresent(date -> addUpdatedDateField(date, controlFieldsBuilder));
        },
        () -> ofNullable(resource.getUpdatedDate())
          .ifPresent(date -> addUpdatedDateField(date, controlFieldsBuilder))
      );
    }
  }

  private void addUpdatedDateField(Date date, ControlFieldsBuilder controlFieldsBuilder) {
    var dateStr = convertDate(date);
    controlFieldsBuilder.addFieldValue(TAG_005, dateStr, 0, dateStr.length());
  }

  private String convertDate(Date date) {
    return MARC_UPDATED_DATE_FORMAT.format(ofEpochMilli(date.getTime()));
  }

  private Optional<Date> chooseDate(Date instanceDate, Date workDate) {
    if (anyNull(instanceDate, workDate)) {
      return Stream.of(instanceDate, workDate)
        .filter(Objects::nonNull)
        .findFirst();
    }
    return instanceDate.after(workDate) ? of(instanceDate) : of(workDate);
  }
}
