package org.folio.marc4ld.service.ld2marc.mapper.datafield.hub;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsFirst;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FAMILY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.JURISDICTION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.MEETING;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;

import java.util.Comparator;
import java.util.List;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class HubCreatorComparator implements Comparator<Resource> {
  private static final List<ResourceTypeDictionary> TYPE_ORDER = List.of(
    PERSON, FAMILY, ORGANIZATION, JURISDICTION, MEETING
  );

  @Override
  public int compare(Resource agent1, Resource agent2) {
    var agent1Index = getTypeOrderIndex(agent1);
    var agent2Index = getTypeOrderIndex(agent2);
    if (agent1Index != agent2Index) {
      return Integer.compare(agent1Index, agent2Index);
    }
    return comparing(Resource::getLabel, nullsFirst(CASE_INSENSITIVE_ORDER))
      .compare(agent1, agent2);
  }

  private int getTypeOrderIndex(Resource agent) {
    return TYPE_ORDER.stream()
      .filter(agent::isOfType)
      .findFirst()
      .map(TYPE_ORDER::indexOf)
      .orElse(TYPE_ORDER.size());
  }
}
