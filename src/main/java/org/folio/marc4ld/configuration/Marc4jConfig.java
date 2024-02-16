package org.folio.marc4ld.configuration;

import static java.util.function.Function.identity;
import static org.folio.marc4ld.util.Constants.DependencyInjection.DATA_FIELD_PREPROCESSORS_MAP;
import static org.folio.marc4ld.util.Constants.DependencyInjection.MARC4LD_MAPPERS_MAP;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.folio.marc4ld.service.mapper.Marc4ldMapper;
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
  public Map<String, DataFieldPreprocessor> dataFieldPreprocessorsMap(
    List<DataFieldPreprocessor> dataFieldPreprocessors) {
    return dataFieldPreprocessors.stream().collect(Collectors.toMap(DataFieldPreprocessor::getTag, identity()));
  }

  @Bean(MARC4LD_MAPPERS_MAP)
  public Map<String, Marc4ldMapper> marc4ldMappersMap(List<Marc4ldMapper> marc4ldMappers) {
    return marc4ldMappers.stream().collect(Collectors.toMap(Marc4ldMapper::getTag, identity()));
  }
}
