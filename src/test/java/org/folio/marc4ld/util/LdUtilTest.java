package org.folio.marc4ld.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ILLUSTRATIONS;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.getLightWeightInstanceResource;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.getSampleInstanceResource;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Set;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@UnitTest
class LdUtilTest {

  @Test
  void getFirstValue_shouldReturnEmptyString_ifGivenSupplierIsEmpty() {
    // given
    var values = new ArrayList<String>();

    // when
    var result = LdUtil.getFirst(values);

    // then
    assertThat(result)
      .isEmpty();
  }

  @Test
  void getFirstValue_shouldReturnFirstValue() {
    // given
    var first = "first";
    var second = "second";
    var values = Lists.newArrayList(first, second);

    // when
    var result = LdUtil.getFirst(values);

    // then
    assertThat(result)
      .isEqualTo(first);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "   "})
  void getFirstValue_shouldReturnSecondValue_ifFirstValueIsEmpty(String first) {
    // given
    var second = "second";
    var values = Lists.newArrayList(first, second);

    // when
    var result = LdUtil.getFirst(values);

    // then
    assertThat(result)
      .isEqualTo(second);
  }

  @Test
  void getFirstValue_shouldReturnEmptyString_ifAllValuesAreNull() {
    // given
    String first = null;
    String second = null;
    var values = Lists.newArrayList(first, second);

    // when
    var result = LdUtil.getFirst(values);

    // then
    assertThat(result)
      .isEmpty();
  }

  @ParameterizedTest
  @NullAndEmptySource
  void resource_isEmpty_ifDocIsNullAndOutGoingIsEmpty(Set<ResourceEdge> edges) {
    //given
    var resource = getSampleInstanceResource();
    resource.setDoc(null);
    resource.setOutgoingEdges(edges);

    //when
    var isEmpty = LdUtil.isEmpty(resource);

    //then
    assertThat(isEmpty)
      .isTrue();
  }

  @Test
  void resource_isNotEmpty_shouldReturnFalse_ifDocIsNullAndOutGoingIsNotEmpty() {
    //given
    var resource = getSampleInstanceResource();
    resource.setDoc(null);

    //when
    var isEmpty = LdUtil.isEmpty(resource);

    //then
    assertThat(isEmpty)
      .isFalse();
  }

  @ParameterizedTest
  @NullAndEmptySource
  void resource_isNotEmpty_shouldReturnFalse_ifDocIsNotNullAndOutGoingIsEmpty(Set<ResourceEdge> edges) {
    //given
    var resource = getSampleInstanceResource();
    resource.setOutgoingEdges(edges);

    //when
    var isEmpty = LdUtil.isEmpty(resource);

    //then
    assertThat(isEmpty)
      .isFalse();
  }

  @Test
  void getWork_shouldReturn_emptyOptional() {
    //given
    var resource = getLightWeightInstanceResource();

    //expect
    assertThat(LdUtil.getWork(resource)).isNotPresent();
  }

  @Test
  void getWork_shouldReturn_work() {
    //given
    var resource = getSampleInstanceResource();

    //when
    var result = LdUtil.getWork(resource);

    //then
    assertThat(result).isPresent();
    assertEquals(Set.of(WORK, BOOKS), result.get().getTypes());
  }

  @Test
  void getOutgoingEdges_shouldReturn_edgesWithSpecifiedPredicate() {
    //given
    var resource = getSampleInstanceResource();

    //when
    var result = LdUtil.getOutgoingEdges(resource, TITLE);

    //then
    assertThat(result).hasSize(5);
  }

  @Test
  void getOutgoingEdges_shouldReturn_emptyList() {
    //given
    var resource = getSampleInstanceResource();

    //when
    var result = LdUtil.getOutgoingEdges(resource, ILLUSTRATIONS);

    //then
    assertThat(result).isEmpty();
  }
}
