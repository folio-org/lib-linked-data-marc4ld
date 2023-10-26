package org.folio.marc2ld.model;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ResourceEdgePk implements Serializable {

  private Long sourceHash;
  private Long targetHash;
  private Long predicateHash;

}
