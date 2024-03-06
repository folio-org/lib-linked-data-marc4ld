package org.folio.marc4ld.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

@AllArgsConstructor
@Getter
public class MarcData {

  private DataField dataField;
  private List<ControlField> controlFields;
}
