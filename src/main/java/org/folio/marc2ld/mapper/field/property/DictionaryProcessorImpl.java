package org.folio.marc2ld.mapper.field.property;

import static java.util.Arrays.stream;
import static org.springframework.core.io.support.ResourcePatternUtils.getResourcePatternResolver;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

@Log4j2
@Service
public class DictionaryProcessorImpl implements DictionaryProcessor, ApplicationListener<ApplicationReadyEvent> {

  private Set<String> dictionaries;

  @Override
  public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
    var resourcePatternResolver = getResourcePatternResolver(new PathMatchingResourcePatternResolver());
    try {
      var resources = resourcePatternResolver.getResources("classpath*:dictionary/*");
      dictionaries = stream(resources)
        .map(Resource::getFilename)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
    } catch (IOException e) {
      log.error("IOException during dictionaries list gathering");
    }
  }

  @Override
  public String check(String property, String value) {
    return dictionaries.stream()
      .filter((property + ".yml")::equals)
      .findFirst()
      .map(fileName -> {
        try (var is = getClass().getResourceAsStream("/dictionary/" + fileName)) {
          return new Yaml().loadAs(is, Map.class);
        } catch (IOException e) {
          log.error("IOException while checking dictionary file for the property [{}]", property);
          return null;
        }
      })
      .filter(d -> d.containsKey(value))
      .map(d -> (String) d.get(value))
      .orElse(value);
  }

}
