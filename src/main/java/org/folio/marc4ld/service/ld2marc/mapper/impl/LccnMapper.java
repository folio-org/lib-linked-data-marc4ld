package org.folio.marc4ld.service.ld2marc.mapper.impl;

import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.marc4ld.util.Constants.SPACE;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
    var dataField = marcFactory.newDataField(TAG, SPACE, SPACE);
    getSubfield(resource)
      .ifPresent(s -> dataField.addSubfield(marcFactory.newSubfield(s, getPropertyValue(NAME, resource))));
    return List.of(dataField);
  }

  private Optional<Character> getSubfield(Resource resource) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> resourceEdge.getPredicate() == STATUS)
      .findFirst()
      .map(e -> getPropertyValue(LINK, e.getTarget()))
      .map(link -> switch (link) {
        case CURRENT -> Optional.of(A);
        case CANCINV -> Optional.of(Z);
        default -> Optional.<Character>empty();
      })
      .orElse(Optional.of(A));
  }

  private String getPropertyValue(PropertyDictionary property, Resource resource) {
    return Optional.of(resource)
      .map(Resource::getDoc)
      .map(doc -> doc.get(property.getValue()))
      .map(node -> node.get(0))
      .map(JsonNode::asText)
      .orElse(StringUtils.EMPTY);
  }
}
