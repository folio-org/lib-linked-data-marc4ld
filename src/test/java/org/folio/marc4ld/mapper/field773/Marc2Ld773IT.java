package org.folio.marc4ld.mapper.field773;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.IS_PART_OF;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.getLiteInstance;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateAbbreviatedTitle;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateAgent;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateIsbn;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLanguageCategory;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLiteInstance;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLiteWork;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLiteWorkTitle;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLocalId;

import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.LinkingEntriesTestHelper;
import org.junit.jupiter.api.Test;

class Marc2Ld773IT extends Marc2LdTestBase {

  @Test
  void shouldMapField773Correctly() {
    //given
    var marc = loadResourceAsString("fields/773/marc_773.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .satisfies(resource -> validateLiteWork(resource, IS_PART_OF, "work title main title"))
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
  void shouldMapField773WithMissingSubfieldsCorrectly() {
    //given
    var marc = loadResourceAsString("fields/773/marc_773_with_missing_subfields.jsonl");

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .satisfies(resource -> validateLiteWork(resource, IS_PART_OF, "work/instance title main title"))
      .satisfies(resource -> validateAgent(resource, "agent name"))
      .satisfies(resource -> validateLiteWorkTitle(resource, "work/instance title main title"))
      .satisfies(resource -> validateLiteInstance(resource, 5, "work/instance title main title"))
      .satisfies(resource -> validateLocalId(resource, "local ID identifier name"))
      .satisfies(resource -> validateIsbn(resource, "ISBN identifier name"))
      .satisfies(LinkingEntriesTestHelper::validateUnknown)
      .satisfies(LinkingEntriesTestHelper::validateLiteInstanceTitle);
  }
}
