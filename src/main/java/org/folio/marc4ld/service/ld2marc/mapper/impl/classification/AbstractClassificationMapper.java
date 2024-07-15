package org.folio.marc4ld.service.ld2marc.mapper.impl.classification;

import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.ITEM_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.SOURCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CLASSIFICATION;
import static org.folio.marc4ld.util.BibframeUtil.getPropertyValue;
import static org.folio.marc4ld.util.BibframeUtil.getPropertyValues;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.B;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.mapper.Ld2MarcMapper;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;

@RequiredArgsConstructor
public abstract class AbstractClassificationMapper implements Ld2MarcMapper {

  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(CLASSIFICATION);

  protected final ObjectMapper objectMapper;
  protected final MarcFactory marcFactory;

  private final String tag;
  private final String source;

  protected abstract char getIndicator1(Resource resource);

  protected abstract char getIndicator2(Resource resource);

  @Override
  public boolean test(ResourceEdge resourceEdge) {
    return resourceEdge.getPredicate() == PredicateDictionary.CLASSIFICATION
      && Objects.equals(resourceEdge.getTarget().getTypes(), SUPPORTED_TYPES)
      && hasCorrespondingSource(resourceEdge.getTarget());
  }

  @Override
  public DataField apply(ResourceEdge resourceEdge) {
    var resource = resourceEdge.getTarget();
    var dataField = marcFactory.newDataField(tag, getIndicator1(resource), getIndicator2(resource));
    getPropertyValues(resource, CODE.getValue(), node -> objectMapper.convertValue(node, new TypeReference<>() {}))
      .stream()
      .map(code -> marcFactory.newSubfield(A, code))
      .forEach(dataField::addSubfield);
    getPropertyValue(resource, ITEM_NUMBER.getValue())
      .map(itemNumber -> marcFactory.newSubfield(B, itemNumber))
      .ifPresent(dataField::addSubfield);
    return dataField;
  }

  protected boolean hasLinkInEdge(Resource resource, PredicateDictionary predicate, String linkValue) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> predicate.equals(resourceEdge.getPredicate()))
      .map(ResourceEdge::getTarget)
      .map(r -> getPropertyValue(r, LINK.getValue()))
      .flatMap(Optional::stream)
      .anyMatch(linkValue::equals);
  }

  private boolean hasCorrespondingSource(Resource resource) {
    return getPropertyValue(resource, SOURCE.getValue())
      .stream()
      .anyMatch(this.source::equals);
  }
}
