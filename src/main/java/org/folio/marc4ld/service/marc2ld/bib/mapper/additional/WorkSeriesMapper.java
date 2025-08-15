package org.folio.marc4ld.service.marc2ld.bib.mapper.additional;

import static org.folio.ld.dictionary.PredicateDictionary.IS_PART_OF;
import static org.folio.marc4ld.util.Constants.TAG_490;
import static org.folio.marc4ld.util.LdUtil.reverseFirstEdgeWithPredicate;

import java.util.List;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.marc2ld.mapper.AdditionalMapper;
import org.springframework.stereotype.Component;

@Component
public class WorkSeriesMapper implements AdditionalMapper {

  private static final List<String> TAGS = List.of(TAG_490);

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  @Override
  public boolean canMap(Marc4LdRules.FieldRule fieldRule) {
    return IS_PART_OF.name().equals(fieldRule.getPredicate());
  }

  @Override
  public void map(MarcData marcData, Resource resource) {
    adjustInstanceSeriesToSeriesRelation(resource);
  }

  private void adjustInstanceSeriesToSeriesRelation(Resource resource) {
    reverseFirstEdgeWithPredicate(resource, PredicateDictionary.INSTANTIATES);
  }
}
