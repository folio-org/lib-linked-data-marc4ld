package org.folio.marc4ld.service.dictionary;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static org.springframework.core.io.support.ResourcePatternUtils.getResourcePatternResolver;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
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

  private Map<String, Map<String, String>> dictionaries;

  @Override
  public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
    var resourcePatternResolver = getResourcePatternResolver(new PathMatchingResourcePatternResolver());
    try {
      var resources = resourcePatternResolver.getResources("classpath*:dictionary/*");
      dictionaries = stream(resources)
        .map(Resource::getFilename)
        .filter(Objects::nonNull)
        .collect(toMap(FilenameUtils::removeExtension, this::readDictionaryResource));
    } catch (IOException e) {
      log.error("IOException during dictionaries gathering", e);
    }
  }

  private Map<String, String> readDictionaryResource(String fileName) {
    try (var is = getClass().getResourceAsStream("/dictionary/" + fileName)) {
      return new Yaml().loadAs(is, Map.class);
    } catch (IOException e) {
      log.error("IOException during reading dictionary [{}]", fileName, e);
      return emptyMap();
    }
  }

  @Override
  public Optional<String> getValue(String dictionary, String key) {
    return Optional.ofNullable(dictionaries.getOrDefault(dictionary, emptyMap()).get(key));
  }

}