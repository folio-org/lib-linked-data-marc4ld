package org.folio.marc4ld.service.ld2marc.mapper.controlfield;

import static org.folio.ld.dictionary.PredicateDictionary.ILLUSTRATIONS;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.LdUtil.getOutgoingEdges;
import static org.folio.marc4ld.util.LdUtil.getPropertyValue;
import static org.folio.marc4ld.util.LdUtil.getWork;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.mapper.CustomControlFieldsMapper;
import org.folio.marc4ld.service.ld2marc.resource.field.ControlFieldsBuilder;
import org.springframework.stereotype.Component;

@Component
public class Ld2MarcIllustrationsMapper implements CustomControlFieldsMapper {
  private static final Set<ResourceTypeDictionary> APPLICABLE_WORK_TYPES = Set.of(BOOKS);
  private static final int START_INDEX = 18;
  private static final int END_INDEX = 22;

  @Override
  public void map(Resource resource, ControlFieldsBuilder controlFieldsBuilder) {
    getWork(resource)
      .filter(work -> APPLICABLE_WORK_TYPES.stream().anyMatch(work::isOfType))
      .ifPresent(work -> {
        var value = getOutgoingEdges(work, ILLUSTRATIONS)
          .stream()
          .limit((long) END_INDEX - (long) START_INDEX)
          .map(ResourceEdge::getTarget)
          .map(r -> getPropertyValue(r, CODE.getValue()))
          .flatMap(Optional::stream)
          .collect(Collectors.joining());
        controlFieldsBuilder.addFieldValue(TAG_008, value, START_INDEX, END_INDEX);
      });
  }
}
