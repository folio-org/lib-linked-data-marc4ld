package org.folio.marc4ld.mapper.test;

import org.folio.marc4ld.service.marc2ld.authority.identifier.IdentifierUrlProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"org.folio.marc4ld", "org.folio.ld.fingerprint"})
public class SpringTestConfig {
  @Bean
  IdentifierUrlProvider testIdentifierUrlProvider() {
    return new TestIdentifierUrlProvider();
  }
}
