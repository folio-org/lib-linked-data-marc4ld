package org.folio.marc4ld.configuration.property;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties
@PropertySource(
  value = "classpath:normalization/bib/marc2ld_bib_last_subfield_rules.yml", factory = YamlPropertySourceFactory.class)
@PropertySource(
  value = "classpath:normalization/bib/marc2ld_bib_subfield_rules.yml", factory = YamlPropertySourceFactory.class)
@PropertySource(
  value = "classpath:normalization/authority/marc2ld_authority_subfield_rules.yaml",
  factory = YamlPropertySourceFactory.class)
@PropertySource(
  value = "classpath:normalization/marc2ld_parentheses_brackets_rules.yaml", factory = YamlPropertySourceFactory.class)
public class Marc2LdNormalizationRules {

  private Map<String, List<String>> bibSubfieldRules;
  private Map<String, List<String>> bibLastSubfieldRules;
  private Map<String, List<String>> authoritySubfieldRules;
  private List<String> parenthesesAndBracketsRules;
}
