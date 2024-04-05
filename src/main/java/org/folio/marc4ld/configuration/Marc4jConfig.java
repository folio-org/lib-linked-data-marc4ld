package org.folio.marc4ld.configuration;

import org.marc4j.marc.MarcFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Marc4jConfig {

  @Bean
  public MarcFactory marcFactory() {
    return MarcFactory.newInstance();
  }
}
