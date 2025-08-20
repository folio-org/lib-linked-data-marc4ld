package org.folio.marc4ld.service.ld2marc.mapper.datafield.identifier;

import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_IAN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_UNKNOWN;
import static org.folio.marc4ld.util.Constants.TAG_024;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
public class OtherStandardIdentifierMapper extends AbstractIdentifierMapper {

  private static final Map<Set<ResourceTypeDictionary>, Character> SUPPORTED_TYPES_WITH_MARC_IND1 = Map.of(
    Set.of(ID_IAN, IDENTIFIER), '3',
    Set.of(ID_UNKNOWN, IDENTIFIER), '8'
  );

  public OtherStandardIdentifierMapper(MarcFactory marcFactory, Comparator<Subfield> subfieldComparator) {
    super(marcFactory, subfieldComparator);
  }

  @Override
  public boolean test(ResourceEdge resourceEdge) {
    return super.test(resourceEdge) && SUPPORTED_TYPES_WITH_MARC_IND1.containsKey(resourceEdge.getTarget().getTypes());
  }

  @Override
  protected String getTag() {
    return TAG_024;
  }

  @Override
  protected char getInd1(ResourceEdge edge) {
    return SUPPORTED_TYPES_WITH_MARC_IND1.get(edge.getTarget().getTypes());
  }

  @Override
  public boolean hasQualifyingInfoSubfield() {
    return true;
  }
}
