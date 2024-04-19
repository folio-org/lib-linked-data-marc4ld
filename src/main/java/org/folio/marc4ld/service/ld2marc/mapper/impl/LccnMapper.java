package org.folio.marc4ld.service.ld2marc.mapper.impl;

import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.mapper.Ld2MarcMapper;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LccnMapper implements Ld2MarcMapper {

  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(ID_LCCN, IDENTIFIER);
  private static final String TAG = "010";
  private static final char EMPTY = ' ';
  private static final String CURRENT = "http://id.loc.gov/vocabulary/mstatus/current";
  private static final String CANCINV = "http://id.loc.gov/vocabulary/mstatus/cancinv";
  private static final char A = 'a';
  private static final char Z = 'z';

  private final MarcFactory marcFactory;

  @Override
  public boolean canMap(PredicateDictionary predicate, Resource resource) {
    return predicate == MAP && Objects.equals(resource.getTypes(), SUPPORTED_TYPES);
  }

  @Override
  public List<DataField> map(Resource resource) {
    var dataField = marcFactory.newDataField(TAG, EMPTY, EMPTY);
    getSubfield(resource)
      .ifPresent(s -> dataField.addSubfield(marcFactory.newSubfield(s, getPropertyValue(NAME, resource))));
    return List.of(dataField);
  }

  private Optional<Character> getSubfield(Resource resource) {
    final var subfield = new AtomicReference<Character>();
    resource.getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> resourceEdge.getPredicate() == STATUS)
      .findFirst()
      .ifPresentOrElse(resourceEdge -> {
        var link = getPropertyValue(LINK, resourceEdge.getTarget());
        if (link.equals(CURRENT)) {
          subfield.set(A);
        } else if (link.equals(CANCINV)) {
          subfield.set(Z);
        }
      }, () -> subfield.set(A));
    return subfield.get() != null ? Optional.of(subfield.get()) : Optional.empty();
  }

  private String getPropertyValue(PropertyDictionary property, Resource resource) {
    return resource.getDoc().get(property.getValue()).get(0).asText();
  }
}
