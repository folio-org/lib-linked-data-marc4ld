package org.folio.marc4ld.configuration.label;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc4ld.service.label.LabelProcessorFactory;
import org.folio.marc4ld.service.label.processor.LabelProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class LabelConfig {

  private final LabelProcessorFactory labelProcessorFactory;

  @Bean
  @SneakyThrows
  public Map<Set<ResourceTypeDictionary>, LabelProcessor> getLabelProcessorMap() {
    var resolver = new PathMatchingResourcePatternResolver();
    var resource = resolver.getResource("classpath:label.yml");
    var inputStream = resource.getInputStream();
    var labelRules = new Yaml()
      .loadAs(inputStream, LabelRules.class);
    return labelRules.getRules()
      .stream()
      .collect(Collectors.toMap(this::getTypes, labelProcessorFactory::get));

  }

  private Set<ResourceTypeDictionary> getTypes(LabelRules.LabelRule rule) {
    return rule.getTypes()
      .stream()
      .map(ResourceTypeDictionary::valueOf)
      .collect(Collectors.toSet());
  }

}
