package org.folio.marc4ld.authority.field100;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FAMILY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCNAF;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TITLE;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.AuthorityValidationHelper.validateIdentifier;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2AuthorityHub100IT extends Marc2LdTestBase {
  @Test
  void shouldMap100FieldToHubWhenTitleSubfieldIsPresent() {
    // given
    var marc = loadResourceAsString("authority/100/marc_100_with_$t.jsonl");

    // when
    var resources = marcAuthorityToResources(marc);

    // then
    assertThat(resources)
      .singleElement()
      .satisfies(r -> assertThat(r.getOutgoingEdges()).hasSize(3))
      .satisfies(this::validateHub)
      .satisfies(this::validateTitle)
      .satisfies(this::validateCreator)
      .satisfies(r -> validateIdentifier(r, "no98055590", ID_LCNAF, "http://id.loc.gov/authorities/no98055590"));
  }

  @Test
  void shouldMap100FieldToHubWithFamilyCreatorWhenInd1Is3AndTitleSubfieldIsPresent() {
    // given
    var marc = loadResourceAsString("authority/100/marc_100_with_$t_family.jsonl");

    // when
    var resources = marcAuthorityToResources(marc);

    // then
    assertThat(resources)
      .singleElement()
      .satisfies(r -> assertThat(r.getOutgoingEdges()).hasSize(3))
      .satisfies(this::validateFamilyHub)
      .satisfies(this::validateFamilyTitle)
      .satisfies(this::validateFamilyCreator)
      .satisfies(r -> validateIdentifier(r, "n2019000001", ID_LCNAF, "http://id.loc.gov/authorities/n2019000001"));
  }

  private void validateCreator(Resource hub) {
    var creator = getFirstOutgoingEdge(hub, e -> e.getPredicate() == CREATOR).getTarget();
    validateResource(
      creator,
      List.of(PERSON),
      Map.of(
        "http://bibfra.me/vocab/lite/date", List.of("1865-1934"),
        "http://bibfra.me/vocab/lite/label", List.of("Lemare, Edwin H., Edwin Henry, 1865-1934"),
        "http://bibfra.me/vocab/lite/name", List.of("Lemare, Edwin H."),
        "http://bibfra.me/vocab/lite/nameAlternative", List.of("Edwin Henry")
      ),
      "Lemare, Edwin H., Edwin Henry, 1865-1934"
    );
  }

  private void validateTitle(Resource hub) {
    var title = getFirstOutgoingEdge(hub, e -> e.getPredicate() == PredicateDictionary.TITLE).getTarget();
    validateResource(
      title,
      List.of(TITLE),
      Map.of(
        "http://bibfra.me/vocab/library/partNumber", List.of("op. 98"),
        "http://bibfra.me/vocab/library/mainTitle", List.of("Toccata and fugue")
      ),
      "Toccata and fugue op. 98"
    );
  }

  private void validateHub(Resource hub) {
    var hubLabel = "Lemare, Edwin H., Edwin Henry, 1865-1934. Toccata and fugue op. 98";
    validateResource(
      hub,
      List.of(HUB),
      Map.of(
        "http://bibfra.me/vocab/lite/medium", List.of("organ"),
        "http://bibfra.me/vocab/library/musicKey", List.of("D minor"),
        "http://bibfra.me/vocab/lite/label", List.of(hubLabel)
      ),
      hubLabel
    );
  }

  private void validateFamilyCreator(Resource hub) {
    var creator = getFirstOutgoingEdge(hub, e -> e.getPredicate() == CREATOR).getTarget();
    validateResource(
      creator,
      List.of(FAMILY),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("House of Windsor"),
        "http://bibfra.me/vocab/library/numeration", List.of("Royal family"),
        "http://bibfra.me/vocab/library/titles", List.of("Dynasty"),
        "http://bibfra.me/vocab/lite/date", List.of("1917-"),
        "http://bibfra.me/vocab/library/miscInfo", List.of("British royal house,"),
        "http://bibfra.me/vocab/library/attribution", List.of("author of,"),
        "http://bibfra.me/vocab/lite/label", List.of("Royal family, House of Windsor, Dynasty, 1917")
      ),
      "Royal family, House of Windsor, Dynasty, 1917"
    );
  }

  private void validateFamilyTitle(Resource hub) {
    var title = getFirstOutgoingEdge(hub, e -> e.getPredicate() == PredicateDictionary.TITLE).getTarget();
    validateResource(
      title,
      List.of(TITLE),
      Map.of(
        "http://bibfra.me/vocab/library/partNumber", List.of("book 1"),
        "http://bibfra.me/vocab/library/mainTitle", List.of("Chronicles of the royal family")
      ),
      "Chronicles of the royal family book 1"
    );
  }

  private void validateFamilyHub(Resource hub) {
    var hubLabel = "Royal family, House of Windsor, Dynasty, 1917. Chronicles of the royal family book 1";
    validateResource(
      hub,
      List.of(HUB),
      Map.of(
        "http://bibfra.me/vocab/lite/medium", List.of("text"),
        "http://bibfra.me/vocab/library/musicKey", List.of("English"),
        "http://bibfra.me/vocab/lite/label", List.of(hubLabel)
      ),
      hubLabel
    );
  }
}
