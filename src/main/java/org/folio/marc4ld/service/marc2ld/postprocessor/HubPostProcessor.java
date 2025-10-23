package org.folio.marc4ld.service.marc2ld.postprocessor;

import static java.lang.String.join;
import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.EXPRESSION_OF;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.marc4ld.util.LdUtil.getOutgoingEdges;
import static org.folio.marc4ld.util.LdUtil.getWork;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HubPostProcessor implements PostProcessor {

  private final FingerprintHashService hashService;
  private final MapperHelper mapperHelper;
  private final ObjectMapper objectMapper;

  @Override
  public void accept(Resource instance, Record marcRecord) {
    var creators = getWorkCreators(instance);
    if (creators.isEmpty()) {
      return;
    }
    getExpressionOfHubs(instance)
      .forEach(hub -> updateHub(hub, creators));
  }

  private void updateHub(Resource hub, List<Resource> creators) {
    var firstCreatorLabel = creators.stream().map(Resource::getLabel).findFirst().orElse("");
    setHubLabel(hub, join(" ", firstCreatorLabel, hub.getLabel()));
    creators
      .forEach(creator -> hub.addOutgoingEdge(new ResourceEdge(hub, creator, CREATOR)));
    hub.setId(hashService.hash(hub));
  }

  private List<Resource> getWorkCreators(Resource instance) {
    return getWork(instance)
      .map(work -> getOutgoingEdges(work, CREATOR))
      .map(creatorsEdges -> creatorsEdges.stream().map(ResourceEdge::getTarget).toList())
      .orElse(List.of());
  }

  private List<Resource> getExpressionOfHubs(Resource instance) {
    return getWork(instance)
      .map(work -> getOutgoingEdges(work, EXPRESSION_OF)
        .stream()
        .map(ResourceEdge::getTarget)
        .filter(r -> r.isOfType(HUB))
        .toList())
      .orElse(List.of());
  }

  public void setHubLabel(Resource hub, String newLabel) {
    var originalProperties = new HashMap<>(mapperHelper.getProperties(hub));
    originalProperties.put(LABEL.getValue(), List.of(newLabel));
    hub.setDoc(objectMapper.convertValue(originalProperties, JsonNode.class));
    hub.setLabel(newLabel);
  }
}
