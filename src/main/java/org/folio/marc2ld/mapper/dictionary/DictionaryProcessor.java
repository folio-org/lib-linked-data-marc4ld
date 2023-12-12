package org.folio.marc2ld.mapper.dictionary;

public interface DictionaryProcessor {

  String getValueIfExists(String property, String key);

  String getKeyIfExists(String property, String value);

}
