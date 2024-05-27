package org.folio.marc4ld.service.ld2marc.mapper.impl.classification;

import static org.folio.ld.dictionary.PredicateDictionary.ASSIGNING_SOURCE;
import static org.folio.ld.dictionary.PropertyDictionary.EDITION;
import static org.folio.ld.dictionary.PropertyDictionary.EDITION_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CLASSIFICATION;
import static org.folio.marc4ld.util.Constants.Classification.ABRIDGED;
import static org.folio.marc4ld.util.Constants.Classification.DLC;
import static org.folio.marc4ld.util.Constants.Classification.FULL;
import static org.folio.marc4ld.util.Constants.Classification.TAG_082;
import static org.folio.marc4ld.util.Constants.FOUR;
import static org.folio.marc4ld.util.Constants.ONE;
import static org.folio.marc4ld.util.Constants.Q;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.TWO;
import static org.folio.marc4ld.util.Constants.ZERO;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.springframework.stereotype.Component;

@Component
public class Ld2MarcDdcClassificationMapper extends AbstractClassificationMapper {

  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(CLASSIFICATION);

  public Ld2MarcDdcClassificationMapper(ObjectMapper objectMapper, MarcFactory marcFactory) {
    super(objectMapper, marcFactory);
  }

  @Override
  public DataField map(Resource resource) {
    var dataField = super.map(resource);
    getPropertyValue(resource, EDITION_NUMBER.getValue())
      .ifPresent(editionNumber -> dataField.addSubfield(marcFactory.newSubfield(TWO, editionNumber)));
    if (dataField.getIndicator2() == FOUR) {
      addSubfieldQ(dataField, resource);
    }
    return dataField;
  }

  @Override
  protected Set<ResourceTypeDictionary> getSupportedTypes() {
    return SUPPORTED_TYPES;
  }

  @Override
  protected String getTag() {
    return TAG_082;
  }

  @Override
  protected char getIndicator1(Resource resource) {
    var ind1 = SPACE;
    if (resource.getDoc().get(EDITION.getValue()) != null) {
      var editions = objectMapper.convertValue(resource.getDoc().get(EDITION.getValue()), List.class);
      if (editions.contains(FULL)) {
        ind1 = ZERO;
      } else if (editions.contains(ABRIDGED)) {
        ind1 = ONE;
      }
    }
    return ind1;
  }

  @Override
  protected char getIndicator2(Resource resource) {
    var ind2 = SPACE;
    if (isAssignedByLc(resource)) {
      ind2 = ZERO;
    }
    if (isAssignedByOtherOrg(resource)) {
      ind2 = FOUR;
    }
    return ind2;
  }

  private boolean isAssignedByLc(Resource resource) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> ASSIGNING_SOURCE.equals(resourceEdge.getPredicate()))
      .map(ResourceEdge::getTarget)
      .filter(r -> r.getDoc().get(LINK.getValue()) != null)
      .anyMatch(r -> DLC.equals(r.getDoc().get(LINK.getValue()).asText()));
  }

  private boolean isAssignedByOtherOrg(Resource resource) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> ASSIGNING_SOURCE.equals(resourceEdge.getPredicate()))
      .map(ResourceEdge::getTarget)
      .anyMatch(r -> r.getDoc().get(LINK.getValue()) == null);
  }

  private void addSubfieldQ(DataField dataField, Resource resource) {
    resource.getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> ASSIGNING_SOURCE.equals(resourceEdge.getPredicate()))
      .map(ResourceEdge::getTarget)
      .findFirst()
      .flatMap(r -> getPropertyValue(r, NAME.getValue()))
      .ifPresent(name -> dataField.addSubfield(marcFactory.newSubfield(Q, name)));
  }
}
