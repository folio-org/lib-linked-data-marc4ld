package org.folio.marc4ld.service.ld2marc.mapper.impl.classification;

import static org.folio.ld.dictionary.PropertyDictionary.ASSIGNER;
import static org.folio.ld.dictionary.PropertyDictionary.STATUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.marc4ld.util.Constants.Classification.DLC;
import static org.folio.marc4ld.util.Constants.Classification.NUBA;
import static org.folio.marc4ld.util.Constants.Classification.TAG_050;
import static org.folio.marc4ld.util.Constants.Classification.UBA;
import static org.folio.marc4ld.util.Constants.ONE;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.ZERO;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.MarcFactory;
import org.springframework.stereotype.Component;

@Component
public class Ld2MarcLcClassificationMapper extends AbstractClassificationMapper {

  private static final String SOURCE = "lc";
  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(CATEGORY);

  public Ld2MarcLcClassificationMapper(MarcFactory marcFactory, ObjectMapper objectMapper) {
    super(objectMapper, marcFactory);
  }

  @Override
  public boolean canMap(PredicateDictionary predicate, Resource resource) {
    return super.canMap(predicate, resource) && isLcClassification(resource);
  }

  @Override
  protected Set<ResourceTypeDictionary> getSupportedTypes() {
    return SUPPORTED_TYPES;
  }

  @Override
  protected String getTag() {
    return TAG_050;
  }

  @Override
  protected char getIndicator1(Resource resource) {
    var ind1 = SPACE;
    if (resource.getDoc().get(STATUS.getValue()) != null) {
      var statuses = objectMapper.convertValue(resource.getDoc().get(STATUS.getValue()), List.class);
      if (statuses.contains(UBA)) {
        ind1 = ZERO;
      } else if (statuses.contains(NUBA)) {
        ind1 = ONE;
      }
    }
    return ind1;
  }

  @Override
  protected char getIndicator2(Resource resource) {
    var ind2 = SPACE;
    if (resource.getDoc().get(ASSIGNER.getValue()) != null) {
      var assigners = objectMapper.convertValue(resource.getDoc().get(ASSIGNER.getValue()), List.class);
      if (assigners.contains(DLC)) {
        ind2 = ZERO;
      }
    }
    return ind2;
  }

  private boolean isLcClassification(Resource resource) {
    return SOURCE.equals(resource.getDoc().get(PropertyDictionary.SOURCE.getValue()).get(0).asText());
  }
}
