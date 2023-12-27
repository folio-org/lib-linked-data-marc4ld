package org.folio.marc4ld.configuration.property;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties
@PropertySource(value = "classpath:marc4bibframe.yml", factory = YamlPropertySourceFactory.class)
public class Marc4BibframeRules {

  private Map<String, List<FieldRule>> fieldRules;

  @Data
  public static class FieldRule {
    private Set<String> types;
    private String parent;
    private String parentPredicate;
    private String predicate;
    private Marc2ldCondition marc2ldCondition;
    private Ld2marcCondition ld2marcCondition;
    private FieldRelation relation;
    private Map<Character, String> subfields;
    private String ind1;
    private String ind2;
    private String label;
    private String concat;
    private boolean append;
    private Map<String, String> constants;
    private Map<String, Map<String, List<Integer>>> controlFields;
    private List<FieldRule> edges;
  }

  @Data
  public static class Marc2ldCondition {
    private Map<Character, String> fields;
    private String ind1;
    private String ind2;
  }

  @Data
  public static class Ld2marcCondition {
    private String edge;
    private boolean skipSubfields;
  }

  @Data
  public static class Ld2marcCondition {
    private String edge;
  }

  @Data
  public static class FieldRelation {
    private char code;
    private char text;
  }
}
