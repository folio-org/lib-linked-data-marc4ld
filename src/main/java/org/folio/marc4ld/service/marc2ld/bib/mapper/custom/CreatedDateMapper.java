package org.folio.marc4ld.service.marc2ld.bib.mapper.custom;

import static org.folio.ld.dictionary.PropertyDictionary.CREATED_DATE;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.LdUtil.getAdminMetadata;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.marc2ld.mapper.CustomMapper;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.Record;

@RequiredArgsConstructor
public class CreatedDateMapper implements CustomMapper {

  private final MapperHelper mapperHelper;

  @Override
  public boolean isApplicable(Record marcRecord) {
    // TODO if it's an instance with a control number? or just an instance
    return true;
  }

  @Override
  public void map(Record marcRecord, Resource instance) {
    marcRecord.getControlFields()
      .stream()
      .filter(controlField -> TAG_008.equals(controlField.getTag()))
      .map(ControlField::getData)
      .filter(data -> data.length() >= 6)
      .map(data -> data.substring(0, 5))
      .findFirst()
      .ifPresent(d -> addCreatedDate(instance, formatMarcDateAsIsoDate(d)));
  }

  private void addCreatedDate(Resource instance, String createdDate) {
    var metadata = getAdminMetadata(instance);
    metadata.ifPresent(m -> {
      var properties = mapperHelper.getProperties(m);
      properties.put(CREATED_DATE.getValue(), List.of(createdDate));
      m.setDoc(mapperHelper.getJsonNode(properties));
    });
    // TODO handle if adminmetadata is not present?
  }

  private String formatMarcDateAsIsoDate(String marcDate) {
    var year = marcDate.substring(0, 1);
    var month = marcDate.substring(2, 3);
    var day = marcDate.substring(4, 5);
    var century = "20";
    if (Integer.parseInt(year) >= 50) {
      century = "19";
    }
    return century + year + "-" + month + "-" + day;
  }
}
