package org.folio.marc4ld.service.ld2marc.mapper.datafield.identifier;

import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.marc4ld.util.Constants.TAG_010;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
public class LccnMapper extends AbstractIdentifierMapper {

  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(ID_LCCN, IDENTIFIER);

  public LccnMapper(MarcFactory marcFactory, Comparator<Subfield> subfieldComparator) {
    super(marcFactory, subfieldComparator);
  }

  @Override
  public boolean test(ResourceEdge resourceEdge) {
    return super.test(resourceEdge) && Objects.equals(resourceEdge.getTarget().getTypes(), SUPPORTED_TYPES);
  }

  @Override
  protected String getTag() {
    return TAG_010;
  }
}
