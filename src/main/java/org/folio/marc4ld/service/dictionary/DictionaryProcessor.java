package org.folio.marc4ld.service.dictionary;

import java.util.List;
import java.util.Optional;

public interface DictionaryProcessor {

  Optional<String> getValue(String dictionary, String key);

  List<String> getValues(String dictionary, List<String> keys);

  Optional<String> getKey(String dictionary, String value);

}
