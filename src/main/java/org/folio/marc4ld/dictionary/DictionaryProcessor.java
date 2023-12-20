package org.folio.marc4ld.dictionary;

import java.util.Optional;

public interface DictionaryProcessor {

  Optional<String> getValue(String dictionary, String key);

}
