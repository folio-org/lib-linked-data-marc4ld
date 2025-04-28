package org.folio.marc4ld.service.marc2ld.mapper.mapper;

import static org.folio.marc4ld.util.Constants.TAG_490;
import static org.folio.marc4ld.util.LdUtil.reverseFirstEdgeWithPredicate;

import java.util.List;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.marc2ld.mapper.Marc2ldMapper;
import org.springframework.stereotype.Component;

@Component
public class WorkSeriesMapper implements Marc2ldMapper {

  private static final List<String> TAGS = List.of(TAG_490);

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  @Override
  public boolean canMap(PredicateDictionary predicate) {
    return predicate == PredicateDictionary.IS_PART_OF;
  }

  @Override
  public void map(MarcData marcData, Resource resource) {
    adjustInstanceSeriesToSeriesRelation(resource);
  }

  private void adjustInstanceSeriesToSeriesRelation(Resource resource) {
    reverseFirstEdgeWithPredicate(resource, PredicateDictionary.INSTANTIATES);
  }
}
