package org.folio.marc4ld.configuration;

import static java.util.function.Function.identity;
import static org.folio.marc4ld.util.Constants.DependencyInjection.DATA_FIELD_PREPROCESSORS_MAP;
import static org.folio.marc4ld.util.Constants.DependencyInjection.MARC2LD_MAPPERS_MAP;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.folio.marc4ld.service.marc2ld.mapper.Marc2ldMapper;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor;
import org.marc4j.marc.MarcFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Marc4jConfig {

  @Bean
  public MarcFactory marcFactory() {
    return MarcFactory.newInstance();
  }

  @Bean(DATA_FIELD_PREPROCESSORS_MAP)
  public Map<String, DataFieldPreprocessor> dataFieldPreprocessorsMap(List<DataFieldPreprocessor> fieldPreprocessors) {
    return fieldPreprocessors.stream()
      .collect(Collectors.toMap(DataFieldPreprocessor::getTag, identity()));
  }

  @Bean(MARC2LD_MAPPERS_MAP)
  public Map<String, List<Marc2ldMapper>> marc2ldMappersMap(List<Marc2ldMapper> marc2LdMappers) {
    return marc2LdMappers.stream()
      .collect(Collectors.groupingBy(Marc2ldMapper::getTag));
  }
}
