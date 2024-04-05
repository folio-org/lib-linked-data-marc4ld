package org.folio.marc4ld.service.dictionary;

import static java.util.Objects.isNull;
import static org.folio.marc4ld.util.Constants.DependencyInjection.DICTIONARY_MAP;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class DictionaryProcessorImpl implements DictionaryProcessor {

  private final Map<String, Map<String, String>> dictionaries;

  @Autowired
  public DictionaryProcessorImpl(
    @Qualifier(DICTIONARY_MAP) Map<String, Map<String, String>> dictionaries
  ) {
    this.dictionaries = dictionaries;
  }

  @Override
  public Optional<String> getValue(String dictionary, String key) {
    if (isNull(key)) {
      return Optional.empty();
    }
    return Optional.ofNullable(dictionary)
      .map(dictionaries::get)
      .map(dic -> dic.get(key));
  }

  @Override
  public Optional<String> getKey(String dictionary, String value) {
    return Optional.ofNullable(dictionary)
      .map(dictionaries::get)
      .flatMap(dic -> this.findFirst(dic, value));
  }

  private Optional<String> findFirst(Map<String, String> dictionary, String value) {
    return dictionary.entrySet()
      .stream()
      .filter(e -> Objects.equals(e.getValue(), value))
      .map(Map.Entry::getKey)
      .findFirst();
  }
}
