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
  value = "classpath:normalization/marc4ld_last_subfield_rules.yml", factory = YamlPropertySourceFactory.class)
@PropertySource(
  value = "classpath:normalization/marc4ld_subfield_rules.yml", factory = YamlPropertySourceFactory.class)
public class Marc2LdNormalizationRules {

  private Map<String, List<String>> subfieldRules;
  private Map<String, List<String>> lastSubfieldRules;
}
