package org.folio.marc4ld.service.ld2marc.mapper.datafield.hub;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FAMILY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.JURISDICTION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.MEETING;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class HubCreatorComparator implements Comparator<Resource> {
  private static final Map<ResourceTypeDictionary, Integer> TYPE_ORDER_INDEX = Map.of(
    PERSON, 0,
    FAMILY, 1,
    ORGANIZATION, 2,
    JURISDICTION, 3,
    MEETING, 4
  );

  @Override
  public int compare(Resource agent1, Resource agent2) {
    var comparator = Comparator.comparingInt(this::getTypeOrderIndex)
      .thenComparing(Resource::getLabel, Comparator.nullsFirst(CASE_INSENSITIVE_ORDER));
    return comparator.compare(agent1, agent2);
  }

  private int getTypeOrderIndex(Resource agent) {
    return agent.getTypes().stream()
      .map(TYPE_ORDER_INDEX::get)
      .filter(Objects::nonNull)
      .min(Integer::compareTo)
      .orElse(TYPE_ORDER_INDEX.size());
  }
}
