package org.folio.marc4ld.mapper.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"org.folio.marc4ld", "org.folio.ld.fingerprint"})
public class SpringTestConfig {
}
