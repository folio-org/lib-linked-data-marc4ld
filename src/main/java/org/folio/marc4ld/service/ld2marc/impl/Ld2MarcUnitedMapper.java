package org.folio.marc4ld.service.ld2marc.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.folio.marc4ld.service.ld2marc.resource.Resource2MarcRecordMapper;
import org.folio.marc4ld.service.ld2marc.resource.impl.Resource2MarcUnitedRecordMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(Ld2MarcUnitedMapper.NAME)
public class Ld2MarcUnitedMapper extends AbstractLd2MarcMapper {

  public static final String NAME = "ld2MarcUnitedMapper";

  public Ld2MarcUnitedMapper(ObjectMapper objectMapper,
                             @Qualifier(Resource2MarcUnitedRecordMapper.NAME)
                             Resource2MarcRecordMapper resourceMapper) {
    super(objectMapper, resourceMapper);
  }
}
