package org.folio.marc2ld.mapper.dictionary;

import java.util.Optional;

public interface DictionaryProcessor {

  Optional<String> getValueIfExists(String dictionary, String key);

}
