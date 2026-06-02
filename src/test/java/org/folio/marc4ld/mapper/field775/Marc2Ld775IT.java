package org.folio.marc4ld.mapper.field775;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.OTHER_EDITION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.LIGHT_RESOURCE;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.getLiteInstance;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.getLiteWork;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateAbbreviatedTitle;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateAgent;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateIsbn;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLanguageCategory;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLccn;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLiteInstance;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLiteWork;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLiteWorkTitle;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLocalId;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateUnknown;

import java.util.stream.Stream;
import org.assertj.core.api.ThrowingConsumer;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.LinkingEntriesTestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Marc2Ld775IT extends Marc2LdTestBase {

  @Test
  void shouldMapField775Correctly() {
    //given
    var marc = loadResourceAsString("fields/775/marc_775.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .satisfies(resource -> validateLiteWork(resource, OTHER_EDITION, "work title main title"))
      .satisfies(resource -> validateAgent(resource, "agent name", "agent name 2"))
      .satisfies(resource -> validateLanguageCategory(resource, "eng", "rus"))
      .satisfies(resource -> validateAbbreviatedTitle(resource, "abbreviated title", "abbreviated title 2"))
      .satisfies(LinkingEntriesTestHelper::validateIssn)
      .satisfies(resource -> validateLiteWorkTitle(resource, "work title main title"))
      .satisfies(resource -> validateLiteInstance(resource, 11, "work title main title"))
      .satisfies(LinkingEntriesTestHelper::validateExtent)
      .satisfies(resource -> validateLocalId(resource, "local ID identifier name", "local ID identifier name 2"))
      .satisfies(LinkingEntriesTestHelper::validateStrn)
      .satisfies(LinkingEntriesTestHelper::validateCoden)
      .satisfies(resource -> validateIsbn(resource, "ISBN identifier name", "ISBN identifier name 2"))
      .satisfies(LinkingEntriesTestHelper::validateUnknown)
      .satisfies(LinkingEntriesTestHelper::validateLccn)
      .satisfies(LinkingEntriesTestHelper::validateLiteInstanceTitle)
      .satisfies(resource -> validateAllIds(getLiteInstance(resource)));
  }

  @Test
  void shouldMapField775WithMissingSubfieldsCorrectly() {
    //given
    var marc = loadResourceAsString("fields/775/marc_775_with_missing_subfields.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .satisfies(resource -> validateLiteWork(resource, OTHER_EDITION, "work/instance title main title"))
      .satisfies(resource -> validateAgent(resource, "agent name"))
      .satisfies(resource -> validateLiteWorkTitle(resource, "work/instance title main title"))
      .satisfies(resource -> validateLiteInstance(resource, 5, "work/instance title main title"))
      .satisfies(resource -> validateLocalId(resource, "local ID identifier name"))
      .satisfies(resource -> validateIsbn(resource, "ISBN identifier name"))
      .satisfies(LinkingEntriesTestHelper::validateUnknown)
      .satisfies(LinkingEntriesTestHelper::validateLiteInstanceTitle);
  }

  @Test
  void shouldMapField775_withoutSubfieldS() {
    //given
    var marc = loadResourceAsString("fields/775/marc_775_without_s.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .satisfies(resource -> validateLiteWork(resource, OTHER_EDITION, "work title from t"));
  }

  @Test
  void shouldMapField775_withoutSubfieldSandT() {
    //given
    var marc = loadResourceAsString("fields/775/marc_775_without_s_and_t.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .satisfies(resource -> validateLiteWork(resource, OTHER_EDITION, "main title sub title"))
      .satisfies(resource -> validateLiteWorkTitle(resource, "main title sub title"));
  }

  @ParameterizedTest
  @MethodSource("provideArguments")
  void shouldMapField775_wSubfieldIsMappedToIdentifier(String fixture, ThrowingConsumer<Resource> assertion) {
    //given
    var marc = loadResourceAsString(fixture);

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result).satisfies(assertion);
  }

  @Test
  void shouldMapField775_linkedResourcesAreLight() {
    //given
    var marc = loadResourceAsString("fields/775/marc_775.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(getLiteWork(result).getTypes()).contains(LIGHT_RESOURCE);
    assertThat(getLiteInstance(result).getTypes()).contains(LIGHT_RESOURCE);
  }

  private static Stream<Arguments> provideArguments() {
    return Stream.of(
      Arguments.of("fields/775/marc_775_w_dlc.jsonl", (ThrowingConsumer<Resource>) r -> validateLccn(r)),
      Arguments.of("fields/775/marc_775_w_unknown.jsonl", (ThrowingConsumer<Resource>) r -> validateUnknown(r))
    );
  }

}
