package org.folio.marc2ld.mapper.field.property;

import java.util.Optional;

public interface DictionaryProcessor {

  Optional<String> getValue(String dictionary, String key);

}
