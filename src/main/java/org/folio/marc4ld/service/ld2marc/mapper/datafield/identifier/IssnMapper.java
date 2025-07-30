package org.folio.marc4ld.service.ld2marc.mapper.datafield.identifier;

import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_ISSN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.util.Constants.TAG_022;
import static org.folio.marc4ld.util.Constants.Y;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.marc4j.marc.MarcFactory;
import org.springframework.stereotype.Component;

@Component
public class IssnMapper extends AbstractIdentifierMapper {

  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(ID_ISSN, IDENTIFIER);
  private static final String INCORRECT = "http://id.loc.gov/vocabulary/mstatus/incorrect";

  public IssnMapper(MarcFactory marcFactory) {
    super(marcFactory);
  }

  @Override
  public boolean test(ResourceEdge resourceEdge) {
    return resourceEdge.getSource() != null
      && resourceEdge.getSource().isOfType(INSTANCE)
      && resourceEdge.getPredicate() == MAP
      && Objects.equals(resourceEdge.getTarget().getTypes(), SUPPORTED_TYPES);
  }

  @Override
  protected String getTag() {
    return TAG_022;
  }

  @Override
  protected Optional<Character> getMarcSubfield(String statusLink) {
    return super.getMarcSubfield(statusLink)
      .or(() -> INCORRECT.equals(statusLink) ? Optional.of(Y) : Optional.empty());
  }
}
