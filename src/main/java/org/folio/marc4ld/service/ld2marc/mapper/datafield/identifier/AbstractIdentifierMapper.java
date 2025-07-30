package org.folio.marc4ld.service.ld2marc.mapper.datafield.identifier;

import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.Z;
import static org.folio.marc4ld.util.LdUtil.getPropertyValue;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.mapper.CustomDataFieldsMapper;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;

@RequiredArgsConstructor
public abstract class AbstractIdentifierMapper implements CustomDataFieldsMapper {

  private static final String CURRENT = "http://id.loc.gov/vocabulary/mstatus/current";
  private static final String CANCINV = "http://id.loc.gov/vocabulary/mstatus/cancinv";

  protected final MarcFactory marcFactory;

  protected abstract String getTag();

  @Override
  public DataField apply(ResourceEdge resourceEdge) {
    var resource = resourceEdge.getTarget();
    var dataField = marcFactory.newDataField(getTag(), SPACE, SPACE);
    getSubfield(resource)
      .flatMap(subfield -> getPropertyValue(resource, NAME.getValue())
        .map(value -> marcFactory.newSubfield(subfield, value))
      )
      .ifPresent(dataField::addSubfield);
    return dataField;
  }

  private Optional<Character> getSubfield(Resource resource) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> resourceEdge.getPredicate() == STATUS)
      .findFirst()
      .flatMap(e -> getPropertyValue(e.getTarget(), LINK.getValue()))
      .map(this::getMarcSubfield)
      .orElse(Optional.of(A));
  }

  protected Optional<Character> getMarcSubfield(String statusLink) {
    return switch (statusLink) {
      case CURRENT -> Optional.of(A);
      case CANCINV -> Optional.of(Z);
      default -> Optional.empty();
    };
  }
}
