package org.folio.marc4ld.service.ld2marc.mapper.custom;

import java.util.List;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.resource.field.ControlFieldsBuilder;
import org.marc4j.marc.DataField;

public interface Ld2MarcCustomMapper {

  void map(Resource resource, Context context);

  record Context(ControlFieldsBuilder controlFieldsBuilder, List<DataField> dataFields) {
  }
}
