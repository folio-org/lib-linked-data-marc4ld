package org.folio.marc4ld.service.dictionary;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@UnitTest
class DictionaryProcessorImplTest {

  @Test
  void getValue_shouldReturnValue() {
    //given
    var key = "key";
    var value = "value";
    var dictionary = "dictionary";
    var processor = initTestProcessor(dictionary, Map.of(key, value));

    //when
    var actualValue = processor.getValue(dictionary, key);

    //then
    assertThat(actualValue)
      .isNotEmpty()
      .contains(value);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void getValue_shouldReturnEmpty_whenDictionaryIsEmpty(String dictionary) {
    //given
    var key = "key";
    var value = "value";
    var existedDictionary = "dictionary";
    var processor = initTestProcessor(existedDictionary, Map.of(key, value));

    //when
    var actualValue = processor.getValue(dictionary, key);

    //then
    assertThat(actualValue)
      .isEmpty();
  }

  @ParameterizedTest
  @NullAndEmptySource
  void getValue_shouldReturnEmpty_whenKeyIsEmpty(String key) {
    //given
    var dictionary = "dictionary";
    var value = "value";
    var existedKey = "key";
    var processor = initTestProcessor(dictionary, Map.of(existedKey, value));

    //when
    var actualValue = processor.getValue(dictionary, key);

    //then
    assertThat(actualValue)
      .isEmpty();
  }

  @Test
  void getKey_shouldReturnKey() {
    //given
    var key = "key";
    var value = "value";
    var dictionary = "dictionary";
    var processor = initTestProcessor(dictionary, Map.of(key, value));

    //when
    var actualKey = processor.getKey(dictionary, value);

    //then
    assertThat(actualKey)
      .isNotEmpty()
      .contains(key);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void getKey_shouldReturnEmpty_whenDictionaryIsEmpty(String dictionary) {
    //given
    var key = "key";
    var value = "value";
    var existedDictionary = "dictionary";
    var processor = initTestProcessor(existedDictionary, Map.of(key, value));

    //when
    var actualKey = processor.getKey(dictionary, value);

    //then
    assertThat(actualKey)
      .isEmpty();
  }

  @ParameterizedTest
  @NullAndEmptySource
  void getKey_shouldReturnEmpty_whenKeyIsEmpty(String value) {
    //given
    var dictionary = "dictionary";
    var existedValue = "value";
    var key = "key";
    var processor = initTestProcessor(dictionary, Map.of(key, existedValue));

    //when
    var actualKey = processor.getKey(dictionary, value);

    //then
    assertThat(actualKey)
      .isEmpty();
  }

  private DictionaryProcessor initTestProcessor(String dictionary, Map<String, String> dictionaryMap) {
    return new DictionaryProcessorImpl(Map.of(dictionary, dictionaryMap));
  }
}
