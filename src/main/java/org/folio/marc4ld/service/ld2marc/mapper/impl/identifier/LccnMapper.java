package org.folio.marc4ld.service.ld2marc.mapper.impl.identifier;

import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.marc4ld.util.Constants.TAG_010;

import java.util.Objects;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.marc4j.marc.MarcFactory;
import org.springframework.stereotype.Component;

@Component
public class LccnMapper extends AbstractIdentifierMapper {

  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(ID_LCCN, IDENTIFIER);

  public LccnMapper(MarcFactory marcFactory) {
    super(marcFactory);
  }

  @Override
  public boolean test(ResourceEdge resourceEdge) {
    return resourceEdge.getPredicate() == MAP && Objects.equals(resourceEdge.getTarget().getTypes(), SUPPORTED_TYPES);
  }

  @Override
  protected String getTag() {
    return TAG_010;
  }
}
