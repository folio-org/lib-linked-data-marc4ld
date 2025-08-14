package org.folio.marc4ld.service.marc2ld.bib.mapper.custom;

import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.folio.ld.dictionary.PredicateDictionary.ADMIN_METADATA;
import static org.folio.ld.dictionary.PropertyDictionary.CREATED_DATE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ANNOTATION;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.LdUtil.getAdminMetadata;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.CustomMapper;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminMetadataCreatedDateMapper implements CustomMapper {

  private final LabelService labelService;
  private final MapperHelper mapperHelper;
  private final FingerprintHashService hashService;

  @Override
  public boolean isApplicable(Record marcRecord) {
    return marcRecord.getControlFields()
      .stream()
      .filter(controlField -> TAG_008.equals(controlField.getTag()))
      .map(ControlField::getData)
      .anyMatch(this::isMarcDate);
  }

  @Override
  public void map(Record marcRecord, Resource instance) {
    marcRecord.getControlFields()
      .stream()
      .filter(controlField -> TAG_008.equals(controlField.getTag()))
      .map(ControlField::getData)
      .filter(this::isMarcDate)
      .map(data -> data.substring(0, 6))
      .findFirst()
      .ifPresent(d -> addCreatedDate(instance, formatMarcDateAsIsoDate(d)));
  }

  protected boolean isMarcDate(String tagData) {
    return tagData.length() >= 6 && isNumeric(tagData.substring(0, 6));
  }

  protected void addCreatedDate(Resource instance, String createdDate) {
    var metadata = getAdminMetadata(instance);
    metadata.ifPresentOrElse(
      m -> appendCreatedDate(m, createdDate),
      () -> createAdminMetadata(instance, createdDate)
    );
  }

  protected void appendCreatedDate(Resource adminMetadata, String createdDate) {
    var properties = mapperHelper.getProperties(adminMetadata);
    properties.put(CREATED_DATE.getValue(), List.of(createdDate));
    adminMetadata.setDoc(mapperHelper.getJsonNode(properties));
    labelService.setLabel(adminMetadata, properties);
    adminMetadata.setId(hashService.hash(adminMetadata));
  }

  protected void createAdminMetadata(Resource instance, String createdDate) {
    var adminMetadata = new Resource();
    adminMetadata.setTypes(Set.of(ANNOTATION));
    appendCreatedDate(adminMetadata, createdDate);
    instance.addOutgoingEdge(new ResourceEdge(instance, adminMetadata, ADMIN_METADATA));
  }

  protected String formatMarcDateAsIsoDate(String marcDate) {
    var year = marcDate.substring(0, 2);
    var month = marcDate.substring(2, 4);
    var day = marcDate.substring(4, 6);
    var century = "20";
    if (Integer.parseInt(year) >= 50) {
      century = "19";
    }
    return "%s%s-%s-%s".formatted(century, year, month, day);
  }
}
