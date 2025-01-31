package org.folio.marc4ld.service.ld2marc.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.folio.marc4ld.service.ld2marc.resource.Resource2MarcRecordMapper;
import org.folio.marc4ld.service.ld2marc.resource.impl.Resource2MarcDividedRecordMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(Ld2MarcDividedMapper.NAME)
public class Ld2MarcDividedMapper extends AbstractLd2MarcMapper {

  public static final String NAME = "ld2MarcDividedMapper";

  public Ld2MarcDividedMapper(ObjectMapper objectMapper,
                              @Qualifier(Resource2MarcDividedRecordMapper.NAME)
                              Resource2MarcRecordMapper resourceMapper) {
    super(objectMapper, resourceMapper);
  }
}
