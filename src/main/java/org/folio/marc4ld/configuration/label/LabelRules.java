package org.folio.marc4ld.configuration.label;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LabelRules {

  private List<LabelRule> rules;

  @Data
  @NoArgsConstructor
  public static class LabelRule {
    private List<String> types;
    private List<String> properties;
    private String predicate;
    private String pattern;
  }
}
