package org.folio.marc4ld;

import org.folio.ld.dictionary.label.LabelGeneratorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LabelGeneratorConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public LabelGeneratorService labelGeneratorService() {
    return new LabelGeneratorService();
  }
}
