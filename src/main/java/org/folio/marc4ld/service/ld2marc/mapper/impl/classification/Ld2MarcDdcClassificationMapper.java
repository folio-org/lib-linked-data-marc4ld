package org.folio.marc4ld.service.ld2marc.mapper.impl.classification;

import static org.folio.ld.dictionary.PredicateDictionary.ASSIGNING_SOURCE;
import static org.folio.ld.dictionary.PropertyDictionary.EDITION;
import static org.folio.ld.dictionary.PropertyDictionary.EDITION_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CLASSIFICATION;
import static org.folio.marc4ld.util.Constants.Classification.ABRIDGED;
import static org.folio.marc4ld.util.Constants.Classification.DLC;
import static org.folio.marc4ld.util.Constants.Classification.FULL;
import static org.folio.marc4ld.util.Constants.Classification.TAG_082;
import static org.folio.marc4ld.util.Constants.FOUR;
import static org.folio.marc4ld.util.Constants.ONE;
import static org.folio.marc4ld.util.Constants.Q;
import static org.folio.marc4ld.util.Constants.SEVEN;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.TWO;
import static org.folio.marc4ld.util.Constants.ZERO;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
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
      getAssignerName(resource).ifPresent(name -> dataField.addSubfield(marcFactory.newSubfield(Q, name)));
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
    var ind1 = SEVEN;
    var edition = getPropertyValue(resource, EDITION.getValue());
    if (edition.isPresent()) {
      if (FULL.equals(edition.get())) {
        ind1 = ZERO;
      } else if (ABRIDGED.equals(edition.get())) {
        ind1 = ONE;
      }
    }
    return ind1;
  }

  @Override
  protected char getIndicator2(Resource resource) {
    var ind2 = SPACE;
    if (hasLinkInEdge(resource, ASSIGNING_SOURCE, DLC)) {
      ind2 = ZERO;
    } else if (getAssignerName(resource).isPresent()) {
      ind2 = FOUR;
    }
    return ind2;
  }

  private Optional<String> getAssignerName(Resource resource) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> ASSIGNING_SOURCE.equals(resourceEdge.getPredicate()))
      .map(ResourceEdge::getTarget)
      .map(r -> getPropertyValue(r, NAME.getValue()))
      .flatMap(Optional::stream)
      .findFirst();
  }
}
