package org.folio.marc4ld.service.ld2marc.mapper.impl.classification;

import static org.folio.ld.dictionary.PropertyDictionary.EDITION;
import static org.folio.ld.dictionary.PropertyDictionary.EDITION_NUMBER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CLASSIFICATION;
import static org.folio.marc4ld.util.Constants.Classification.ABRIDGED;
import static org.folio.marc4ld.util.Constants.Classification.FULL;
import static org.folio.marc4ld.util.Constants.Classification.TAG_082;
import static org.folio.marc4ld.util.Constants.ONE;
import static org.folio.marc4ld.util.Constants.Q;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.TWO;
import static org.folio.marc4ld.util.Constants.ZERO;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.springframework.stereotype.Component;

@Component
public class Ld2MarcDdcClassificationMapper extends AbstractClassificationMapper {

  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(CLASSIFICATION);
  private static final String US_LOC = "United States, Library of Congress";

  public Ld2MarcDdcClassificationMapper(ObjectMapper objectMapper, MarcFactory marcFactory) {
    super(objectMapper, marcFactory);
  }

  @Override
  public DataField map(Resource resource) {
    var dataField = super.map(resource);
    getPropertyValue(resource, EDITION_NUMBER.getValue())
      .ifPresent(editionNumber -> dataField.addSubfield(marcFactory.newSubfield(TWO, editionNumber)));
    getSubresourceWithLabel(resource, r -> !US_LOC.equals(r.getLabel()))
      .ifPresent(r -> dataField.addSubfield(marcFactory.newSubfield(Q, r.getLabel())));
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
    return getSubresourceWithLabel(resource, r -> US_LOC.equals(r.getLabel()))
      .map(r -> ZERO)
      .orElse(SPACE);
  }

  private Optional<Resource> getSubresourceWithLabel(Resource resource, Predicate<Resource> labelPredicate) {
    return resource.getOutgoingEdges()
      .stream()
      .map(ResourceEdge::getTarget)
      .filter(labelPredicate)
      .findAny();
  }
}
