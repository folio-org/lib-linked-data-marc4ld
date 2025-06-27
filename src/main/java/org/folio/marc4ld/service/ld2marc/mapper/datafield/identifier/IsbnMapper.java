package org.folio.marc4ld.service.ld2marc.mapper.datafield.identifier;

import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PropertyDictionary.QUALIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_ISBN;
import static org.folio.marc4ld.util.Constants.Q;
import static org.folio.marc4ld.util.Constants.TAG_020;
import static org.folio.marc4ld.util.LdUtil.getPropertyValues;

import java.util.Objects;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.springframework.stereotype.Component;

@Component
public class IsbnMapper extends AbstractIdentifierMapper {

  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(ID_ISBN, IDENTIFIER);

  public IsbnMapper(MarcFactory marcFactory) {
    super(marcFactory);
  }

  @Override
  public boolean test(ResourceEdge resourceEdge) {
    return resourceEdge.getPredicate() == MAP && Objects.equals(resourceEdge.getTarget().getTypes(), SUPPORTED_TYPES);
  }

  @Override
  protected String getTag() {
    return TAG_020;
  }

  @Override
  public DataField apply(ResourceEdge resourceEdge) {
    var dataField = super.apply(resourceEdge);
    getPropertyValues(resourceEdge.getTarget(), QUALIFIER.getValue())
      .stream()
      .map(qualifier -> marcFactory.newSubfield(Q, qualifier))
      .forEach(dataField::addSubfield);
    return dataField;
  }
}
