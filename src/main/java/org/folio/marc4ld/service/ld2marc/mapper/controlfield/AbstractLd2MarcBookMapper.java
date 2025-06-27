package org.folio.marc4ld.service.ld2marc.mapper.controlfield;

import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.LdUtil.getOutgoingEdges;
import static org.folio.marc4ld.util.LdUtil.getPropertyValue;
import static org.folio.marc4ld.util.LdUtil.getWork;

import java.util.Optional;
import java.util.stream.Collectors;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.mapper.CustomControlFieldsMapper;
import org.folio.marc4ld.service.ld2marc.resource.field.ControlFieldsBuilder;

public abstract class AbstractLd2MarcBookMapper implements CustomControlFieldsMapper {

  protected abstract PredicateDictionary getPredicate();

  protected abstract int getStartIndex();

  protected abstract int getEndIndex();

  @Override
  public void map(Resource resource, ControlFieldsBuilder controlFieldsBuilder) {
    getWork(resource)
      .ifPresent(work -> {
        var value = getOutgoingEdges(work, getPredicate())
          .stream()
          .limit((long) getEndIndex() - (long) getStartIndex())
          .map(ResourceEdge::getTarget)
          .map(r -> getPropertyValue(r, CODE.getValue()))
          .flatMap(Optional::stream)
          .collect(Collectors.joining());
        controlFieldsBuilder.addFieldValue(TAG_008, value, getStartIndex(), getEndIndex());
      });
  }
}
