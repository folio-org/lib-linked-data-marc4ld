package org.folio.marc4ld.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class IdentifierTypesTest {

  @ParameterizedTest
  @EnumSource(value = ResourceTypeDictionary.class,
    mode = EnumSource.Mode.INCLUDE,
    names = {"INSTANCE", "WORK"})
  void containsBibliographicTypes(ResourceTypeDictionary type) {
    //when
    var expectedInstanceTypes = IdentifierTypes.BIBLIOGRAPHIC_TYPES;

    //then
    assertThat(expectedInstanceTypes)
      .isNotEmpty()
      .contains(type);
  }

  @ParameterizedTest
  @EnumSource(value = ResourceTypeDictionary.class,
    mode = EnumSource.Mode.EXCLUDE,
    names = {"INSTANCE", "WORK"})
  void notContainsBibliographicTypes(ResourceTypeDictionary type) {
    //when
    var expectedInstanceTypes = IdentifierTypes.BIBLIOGRAPHIC_TYPES;

    //then
    assertThat(expectedInstanceTypes)
      .isNotEmpty()
      .doesNotContain(type);
  }

  @ParameterizedTest
  @EnumSource(value = ResourceTypeDictionary.class,
    mode = EnumSource.Mode.INCLUDE,
    names = {"CONCEPT", "PERSON", "FAMILY"})
  void containsAuthorityTypes(ResourceTypeDictionary type) {
    //when
    var expectedAuthorityTypes = IdentifierTypes.AUTHORITY_TYPES;

    //then
    assertThat(expectedAuthorityTypes)
      .isNotEmpty()
      .contains(type);
  }

  @ParameterizedTest
  @EnumSource(value = ResourceTypeDictionary.class,
    mode = EnumSource.Mode.EXCLUDE,
    names = {"CONCEPT", "PERSON", "FAMILY"})
  void notContainsAuthorityTypes(ResourceTypeDictionary type) {
    //when
    var expectedAuthorityTypes = IdentifierTypes.AUTHORITY_TYPES;
    //then
    assertThat(expectedAuthorityTypes)
      .isNotEmpty()
      .doesNotContain(type);
  }
}
