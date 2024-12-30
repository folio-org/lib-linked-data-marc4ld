package org.folio.marc4ld.service.marc2ld.mapper.custom;

import java.util.Set;
import org.folio.ld.dictionary.model.Resource;

public interface CustomMapper {

  Set<Character> APPLICABLE_TYPES = Set.of('a', 't');
  Set<Character> APPLICABLE_LEVELS = Set.of('a', 'c', 'd', 'm');

  default boolean isApplicable(org.marc4j.marc.Record marcRecord) {
    return APPLICABLE_TYPES.contains(marcRecord.getLeader().getTypeOfRecord())
      && APPLICABLE_LEVELS.contains(marcRecord.getLeader().getImplDefined1()[0]);
  }

  void map(org.marc4j.marc.Record marcRecord, Resource instance);
}
