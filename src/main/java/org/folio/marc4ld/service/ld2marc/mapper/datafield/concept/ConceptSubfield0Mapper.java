package org.folio.marc4ld.service.ld2marc.mapper.datafield.concept;

import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.STATUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.util.Constants.ZERO;
import static org.folio.marc4ld.util.LdUtil.getPropertyValue;
import static org.folio.marc4ld.util.MarcUtil.addSubfieldIfNotDuplicate;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.mapper.AdditionalDataFieldsMapper;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConceptSubfield0Mapper implements AdditionalDataFieldsMapper {
  private static final String STATUS_CURRENT = "http://id.loc.gov/vocabulary/mstatus/current";
  private final MarcFactory marcFactory;

  @Override
  public boolean test(ResourceEdge resourceEdge) {
    return resourceEdge.getSource() != null
      && resourceEdge.getSource().isOfType(WORK)
      && resourceEdge.getPredicate() == SUBJECT
      && resourceEdge.getTarget().isOfType(CONCEPT);
  }

  @Override
  public DataField apply(ResourceEdge resourceEdge, DataField mappedSoFar) {
    var conceptResource = resourceEdge.getTarget();
    getIdentifierResources(conceptResource).stream()
      .flatMap(identifierResource -> getPropertyValue(identifierResource, LINK.getValue()).stream())
      .sorted()
      .forEach(identifierLink -> addSubfieldIfNotDuplicate(mappedSoFar, marcFactory.newSubfield(ZERO, identifierLink)));

    return mappedSoFar;
  }

  private Set<Resource> getIdentifierResources(Resource conceptResource) {
    var result = conceptResource.getOutgoingEdges()
      .stream()
      .filter(edge -> edge.getPredicate() == MAP)
      .map(ResourceEdge::getTarget)
      .filter(resource -> resource.isOfType(IDENTIFIER))
      .filter(this::isCurrentIdentifier)
      .collect(Collectors.toSet());
    if (result.isEmpty()) {
      return findFocusIdentifierResources(conceptResource);
    }
    return result;
  }

  private Set<Resource> findFocusIdentifierResources(Resource conceptResource) {
    if (isIntermediateConcept(conceptResource)) {
      return conceptResource.getOutgoingEdges()
        .stream()
        .filter(edge -> edge.getPredicate() == FOCUS)
        .map(ResourceEdge::getTarget)
        .flatMap(resource -> getIdentifierResources(resource).stream())
        .collect(Collectors.toSet());
    }
    return Set.of();
  }

  private boolean isIntermediateConcept(Resource conceptResource) {
    return hasFocusEdge(conceptResource) && hasNoSubFocusEdges(conceptResource);
  }

  private static boolean hasFocusEdge(Resource conceptResource) {
    return conceptResource.getOutgoingEdges().stream()
      .filter(edge -> edge.getPredicate() == FOCUS)
      .count() == 1;
  }

  private boolean hasNoSubFocusEdges(Resource resource) {
    return resource.getOutgoingEdges().stream()
      .map(edge -> edge.getPredicate().getUri())
      .noneMatch(predicate -> predicate.equals(SUB_FOCUS.getUri()));
  }

  private boolean isCurrentIdentifier(Resource identifierResource) {
    return identifierResource.getOutgoingEdges().isEmpty()
      || identifierResource.getOutgoingEdges()
        .stream()
        .map(ResourceEdge::getTarget)
        .filter(target -> target.isOfType(STATUS))
        .flatMap(target -> getPropertyValue(target, LINK.getValue()).stream())
        .anyMatch(STATUS_CURRENT::equalsIgnoreCase);
  }
}
