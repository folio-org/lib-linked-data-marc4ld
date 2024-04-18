package org.folio.marc4ld.service.marc2ld.postprocessor.impl;

import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.marc4ld.util.BibframeUtil.getFirst;
import static org.folio.marc4ld.util.BibframeUtil.isNotEmpty;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.marc2ld.postprocessor.InstanceResourcePostProcessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstanceResourcePostProcessorImpl implements InstanceResourcePostProcessor {
  private final FingerprintHashService hashService;

  @Override
  public Resource apply(Resource instance) {
    instance.setLabel(selectInstanceLabel(instance));
    setWorkLabel(instance);
    cleanEmptyEdges(instance);
    setId(instance);
    return instance;
  }

  private String selectInstanceLabel(Resource instance) {
    var labels = instance.getOutgoingEdges().stream()
      .filter(e -> Objects.equals(TITLE.getUri(), e.getPredicate().getUri()))
      .map(re -> re.getTarget().getLabel())
      .toList();
    return getFirst(labels);
  }

  private void setWorkLabel(Resource instance) {
    instance.getOutgoingEdges()
      .stream()
      .filter(re -> INSTANTIATES.equals(re.getPredicate()))
      .findFirst()
      .map(ResourceEdge::getTarget)
      .ifPresent(work -> work.setLabel(instance.getLabel()));
  }

  private void cleanEmptyEdges(Resource resource) {
    resource.setOutgoingEdges(resource.getOutgoingEdges().stream()
      .map(re -> {
        cleanEmptyEdges(re.getTarget());
        return re;
      })
      .filter(re -> isNotEmpty(re.getTarget()))
      .collect(Collectors.toCollection(LinkedHashSet::new))
    );
  }

  private void setId(Resource resource) {
    resource.setId(hashService.hash(resource));
    resource.getOutgoingEdges()
      .forEach(re -> setId(re.getTarget()));
  }
}
