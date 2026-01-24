package org.folio.marc4ld.service.ld2marc.mapper.datafield.hub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FAMILY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.JURISDICTION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.MEETING;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;

import java.util.ArrayList;
import java.util.List;
import org.folio.ld.dictionary.model.Resource;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;

@UnitTest
class HubCreatorComparatorTest {

  @Test
  void shouldSortResourcesByTypeOrder() {
    // given
    var person = new Resource().setLabel("Alice").addType(PERSON);
    var family = new Resource().setLabel("Brown").addType(FAMILY);
    var organization = new Resource().setLabel("Zoo").addType(ORGANIZATION);
    var meeting = new Resource().setLabel("Alpha").addType(MEETING);
    var jurisdiction = new Resource().setLabel("Delta").addType(JURISDICTION);
    var place = new Resource().setLabel("United States").addType(PLACE);

    var resources = new ArrayList<>(List.of(organization, family, place, meeting, person, jurisdiction));

    // when
    resources.sort(new HubCreatorComparator());

    // then
    assertThat(resources.get(0)).isSameAs(person);
    assertThat(resources.get(1)).isSameAs(family);
    assertThat(resources.get(2)).isSameAs(organization);
    assertThat(resources.get(3)).isSameAs(jurisdiction);
    assertThat(resources.get(4)).isSameAs(meeting);
    assertThat(resources.get(5)).isSameAs(place);
  }

  @Test
  void shouldSortByLabelIfSameType() {
    // given
    var personA = new Resource().setLabel("Alice").addType(PERSON);
    var personB = new Resource().setLabel("Bob").addType(PERSON);
    var resources = new ArrayList<>(List.of(personB, personA));

    // when
    resources.sort(new HubCreatorComparator());

    // then
    assertThat(resources.get(0)).isSameAs(personA);
    assertThat(resources.get(1)).isSameAs(personB);
  }
}
