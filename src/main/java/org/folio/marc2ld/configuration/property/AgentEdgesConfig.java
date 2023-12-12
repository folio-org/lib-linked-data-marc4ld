package org.folio.marc2ld.configuration.property;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:agent-edges.yml", factory = YamlPropertySourceFactory.class)
public class AgentEdgesConfig {

  @Bean
  @ConfigurationProperties("agent-edges")
  public Map<String, String> agentEdges() {
    return new HashMap<>();
  }
}
