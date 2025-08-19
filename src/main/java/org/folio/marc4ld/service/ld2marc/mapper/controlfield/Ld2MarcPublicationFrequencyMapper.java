package org.folio.marc4ld.service.ld2marc.mapper.controlfield;

import static org.folio.ld.dictionary.PredicateDictionary.PUBLICATION_FREQUENCY;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONTINUING_RESOURCES;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.LdUtil.getOutgoingEdges;
import static org.folio.marc4ld.util.LdUtil.getPropertyValue;
import static org.folio.marc4ld.util.LdUtil.getWork;
import static org.folio.marc4ld.util.LdUtil.isInstance;

import java.util.Optional;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.mapper.CustomControlFieldsMapper;
import org.folio.marc4ld.service.ld2marc.resource.field.ControlFieldsBuilder;
import org.springframework.stereotype.Component;

@Component
public class Ld2MarcPublicationFrequencyMapper implements CustomControlFieldsMapper {
  private static final Set<ResourceTypeDictionary> APPLICABLE_WORK_TYPES = Set.of(CONTINUING_RESOURCES);
  private static final Set<String> CODES_19 = Set.of("n", "x");

  @Override
  public void map(Resource resource, ControlFieldsBuilder controlFieldsBuilder) {
    if (isInstance(resource) && isApplicableWorkType(resource)) {
      getOutgoingEdges(resource, PUBLICATION_FREQUENCY)
        .stream()
        .map(ResourceEdge::getTarget)
        .map(r -> getPropertyValue(r, CODE.getValue()))
        .flatMap(Optional::stream)
        .forEach(code -> writeCode(code, controlFieldsBuilder));
    }
  }

  private boolean isApplicableWorkType(Resource resource) {
    return getWork(resource)
      .stream()
      .anyMatch(work -> APPLICABLE_WORK_TYPES.stream().anyMatch(work::isOfType));
  }

  private void writeCode(String code, ControlFieldsBuilder controlFieldsBuilder) {
    var index = CODES_19.contains(code) ? 19 : 18;
    controlFieldsBuilder.addFieldValue(TAG_008, code, index, index + 1);
  }
}
