package org.folio.marc4ld.service.marc2ld.relation;

import static org.folio.marc4ld.util.LdUtil.isNotEmpty;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.springframework.stereotype.Component;

@Component
public class EmptyEdgesCleanerImpl implements EmptyEdgesCleaner {

  @Override
  public Resource apply(Resource resource) {
    accept(resource);
    return resource;
  }

  @Override
  public void accept(Resource resource) {
    var outEdges = resource.getOutgoingEdges().stream()
      .map(this::clean)
      .filter(re -> isNotEmpty(re.getTarget()))
      .collect(Collectors.toCollection(LinkedHashSet::new));
    resource.setOutgoingEdges(outEdges);
  }

  private ResourceEdge clean(ResourceEdge edge) {
    this.accept(edge.getTarget());
    return edge;
  }
}
