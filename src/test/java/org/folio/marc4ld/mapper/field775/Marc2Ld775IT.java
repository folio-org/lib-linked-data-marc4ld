package org.folio.marc4ld.mapper.field775;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.OTHER_EDITION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ABBREVIATED_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.LANGUAGE_CATEGORY;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.getEdges;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.getLiteInstance;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.getLiteWork;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateAgent;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateIsbn;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLiteInstance;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLiteWork;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLiteWorkTitle;
import static org.folio.marc4ld.test.helper.LinkingEntriesTestHelper.validateLocalId;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.test.helper.LinkingEntriesTestHelper;
import org.junit.jupiter.api.Test;

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

  private void validateLanguageCategory(Resource resource, String... expectedCodes) {
    var resourceEdges = getEdges(getLiteWork(resource), LANGUAGE_CATEGORY);
    assertThat(resourceEdges).hasSize(expectedCodes.length);
    var index = 0;
    for (var expectedCode : expectedCodes) {
      validateEdge(resourceEdges.get(index), LANGUAGE,
        List.of(LANGUAGE_CATEGORY),
        Map.of(
          "http://bibfra.me/vocab/library/code", List.of(expectedCode),
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/languages/" + expectedCode)
        ), expectedCode);
      index++;
    }
  }

  private void validateAbbreviatedTitle(Resource resource, String... expectedTitles) {
    var resourceEdges = getEdges(getLiteWork(resource), ABBREVIATED_TITLE);
    assertThat(resourceEdges).hasSize(expectedTitles.length);
    var index = 0;
    for (var expectedTitle : expectedTitles) {
      validateEdge(resourceEdges.get(index), PredicateDictionary.TITLE,
        List.of(ABBREVIATED_TITLE),
        Map.of(
          "http://bibfra.me/vocab/library/mainTitle", List.of(expectedTitle)
        ), expectedTitle);
      index++;
    }
  }
}
