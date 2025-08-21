package org.folio.marc4ld.service.ld2marc.mapper.datafield.identifier;

import static java.util.stream.Stream.concat;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.QUALIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.Q;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.Z;
import static org.folio.marc4ld.util.LdUtil.getPropertyValue;
import static org.folio.marc4ld.util.LdUtil.getPropertyValues;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.mapper.CustomDataFieldsMapper;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;

@RequiredArgsConstructor
public abstract class AbstractIdentifierMapper implements CustomDataFieldsMapper {

  private static final String CURRENT = "http://id.loc.gov/vocabulary/mstatus/current";
  private static final String CANCINV = "http://id.loc.gov/vocabulary/mstatus/cancinv";

  private final MarcFactory marcFactory;
  private final Comparator<Subfield> subfieldComparator;

  protected abstract String getTag();

  @Override
  public boolean test(ResourceEdge resourceEdge) {
    return resourceEdge.getSource() != null
      && resourceEdge.getSource().isOfType(INSTANCE)
      && resourceEdge.getPredicate() == MAP;
  }

  @Override
  public DataField apply(ResourceEdge resourceEdge) {
    var resource = resourceEdge.getTarget();
    var dataField = marcFactory.newDataField(getTag(), getInd1(resourceEdge), SPACE);

    var idSubfields = getIdSubfieldChar(resource)
      .flatMap(
        subfieldChar -> getPropertyValue(resource, NAME.getValue())
          .map(value -> marcFactory.newSubfield(subfieldChar, value))
      )
      .stream();

    var qualifierSubfields = hasQualifyingInfoSubfield()
      ? getPropertyValues(resource, QUALIFIER.getValue())
        .stream()
        .map(val -> marcFactory.newSubfield(Q, val))
      : Stream.<Subfield>empty();

    concat(idSubfields, qualifierSubfields)
      .sorted(subfieldComparator)
      .forEach(dataField::addSubfield);

    return dataField;
  }

  private Optional<Character> getIdSubfieldChar(Resource resource) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> resourceEdge.getPredicate() == STATUS)
      .findFirst()
      .flatMap(e -> getPropertyValue(e.getTarget(), LINK.getValue()))
      .map(this::getMarcSubfield)
      .orElse(Optional.of(A));
  }

  @SuppressWarnings("squid:S1172")
  protected char getInd1(ResourceEdge edge) {
    return SPACE;
  }

  protected boolean hasQualifyingInfoSubfield() {
    return false;
  }

  protected Optional<Character> getMarcSubfield(String statusLink) {
    return switch (statusLink) {
      case CURRENT -> Optional.of(A);
      case CANCINV -> Optional.of(Z);
      default -> Optional.empty();
    };
  }
}
