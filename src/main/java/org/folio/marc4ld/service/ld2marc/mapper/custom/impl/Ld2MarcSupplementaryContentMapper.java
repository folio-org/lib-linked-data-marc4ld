package org.folio.marc4ld.service.ld2marc.mapper.custom.impl;

import static org.folio.ld.dictionary.PredicateDictionary.SUPPLEMENTARY_CONTENT;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.marc4ld.service.marc2ld.mapper.custom.impl.category.SupplementaryContentMapper.CODE_TO_LINK_SUFFIX_MAP;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.LdUtil.getOutgoingEdges;
import static org.folio.marc4ld.util.LdUtil.getPropertyValue;
import static org.folio.marc4ld.util.LdUtil.getWork;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.mapper.custom.Ld2MarcCustomMapper;
import org.springframework.stereotype.Component;

@Component
public class Ld2MarcSupplementaryContentMapper implements Ld2MarcCustomMapper {

  private static final String INDEX = "index";

  @Override
  public void map(Resource resource, Context context) {
    getWork(resource)
      .ifPresent(work -> {
        var supplementaryContentEdges = getOutgoingEdges(work, SUPPLEMENTARY_CONTENT);
        var nonIndexCodes = getNonIndexCodes(supplementaryContentEdges);
        if (!nonIndexCodes.isEmpty()) {
          context.controlFieldsBuilder().addFieldValue(TAG_008, nonIndexCodes, 24, 28);
        }
        if (hasIndex(supplementaryContentEdges)) {
          context.controlFieldsBuilder().addFieldValue(TAG_008, "1", 31, 32);
        }
      });
  }

  private String getNonIndexCodes(List<ResourceEdge> supplementaryContentEdges) {
    return supplementaryContentEdges.stream()
      .map(ResourceEdge::getTarget)
      .map(r -> getPropertyValue(r, CODE.getValue()))
      .flatMap(Optional::stream)
      .distinct()
      .filter(codeValue -> CODE_TO_LINK_SUFFIX_MAP.containsValue(codeValue) && !INDEX.equals(codeValue))
      .flatMap(codeValue -> CODE_TO_LINK_SUFFIX_MAP.entrySet()
        .stream()
        .filter(entry -> codeValue.equals(entry.getValue()))
        .map(Map.Entry::getKey)
        .map(String::valueOf)
      )
      .collect(Collectors.joining());
  }

  private boolean hasIndex(List<ResourceEdge> edges) {
    return edges.stream()
      .map(ResourceEdge::getTarget)
      .map(r -> getPropertyValue(r, CODE.getValue()))
      .flatMap(Optional::stream)
      .anyMatch(INDEX::equals);
  }
}
