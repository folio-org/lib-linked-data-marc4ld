package org.folio.marc4ld.configuration;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static org.folio.marc4ld.util.Constants.DependencyInjection.DICTIONARY_MAP;
import static org.springframework.core.io.support.ResourcePatternUtils.getResourcePatternResolver;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

@Log4j2
@Configuration
public class DictionaryConfig {

  @Bean(DICTIONARY_MAP)
  public Map<String, Map<String, String>> getDictionaryProcessor() {
    var resourcePatternResolver = getResourcePatternResolver(new PathMatchingResourcePatternResolver());
    var resources = new Resource[0];
    try {
      resources = resourcePatternResolver.getResources("classpath*:dictionary/*");
    } catch (IOException e) {
      log.error("IOException during dictionaries gathering {}", e.getMessage());
      throw new RuntimeException(e);
    }
    return stream(resources)
      .map(Resource::getFilename)
      .filter(Objects::nonNull)
      .collect(toMap(FilenameUtils::removeExtension, this::readDictionaryResource));
  }

  private Map<String, String> readDictionaryResource(String fileName) {
    try (var is = getClass().getResourceAsStream("/dictionary/" + fileName)) {
      return new Yaml().loadAs(is, Map.class);
    } catch (IOException e) {
      log.error("IOException during reading dictionary [{}]", fileName, e);
      return emptyMap();
    }
  }
}
