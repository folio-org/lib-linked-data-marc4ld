package org.folio.marc4ld.service.marc2ld.mapper.mapper;

import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.OTHER_VERSION;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_UNKNOWN;
import static org.folio.marc4ld.util.Constants.S;
import static org.folio.marc4ld.util.Constants.T;
import static org.folio.marc4ld.util.Constants.TAG_776;
import static org.folio.marc4ld.util.MarcUtil.getSubfieldValue;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.marc2ld.mapper.Marc2ldMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtherVersionMapper implements Marc2ldMapper {

  private static final List<String> TAGS = List.of(TAG_776);
  private static final Set<ResourceTypeDictionary> IDENTIFIER_TYPE = Set.of(IDENTIFIER);
  private static final String DLC_PREFIX = "(DLC)";

  private final MapperHelper mapperHelper;
  private final FingerprintHashService hashService;

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  @Override
  public boolean canMap(PredicateDictionary predicate) {
    return predicate == OTHER_VERSION;
  }

  @Override
  public void map(MarcData marcData, Resource resource) {
    var dataField = marcData.getDataField();
    mapWork(resource, getSubfieldValue(S, dataField));
    mapInstance(resource, getSubfieldValue(T, dataField));
    adjustWorkToInstanceRelation(resource);
  }

  private void mapWork(Resource resource, String title) {
    var properties = mapperHelper.getProperties(resource);
    properties.put(LABEL.getValue(), List.of(title));
    resource.setDoc(mapperHelper.getJsonNode(properties));
    resource.setLabel(title);
  }

  private void mapInstance(Resource resource, String title) {
    resource.getOutgoingEdges()
      .stream()
      .filter(re -> INSTANTIATES.equals(re.getPredicate()))
      .findFirst()
      .map(ResourceEdge::getTarget)
      .ifPresent(instanceResource -> {
        instanceResource.setLabel(title);
        adjustIdentifiers(instanceResource);
        instanceResource.setId(hashService.hash(instanceResource));
      });
  }

  private void adjustIdentifiers(Resource resource) {
    resource.getOutgoingEdges()
      .stream()
      .filter(re -> MAP.equals(re.getPredicate()))
      .map(ResourceEdge::getTarget)
      .filter(r -> IDENTIFIER_TYPE.equals(r.getTypes()))
      .forEach(this::adjustIdentifier);
  }

  private void adjustIdentifier(Resource resource) {
    var properties = mapperHelper.getProperties(resource);
    var name = properties.get(NAME.getValue()).get(0);
    if (name.startsWith(DLC_PREFIX)) {
      name = name.substring(DLC_PREFIX.length());
      properties.put(NAME.getValue(), List.of(name));
      resource.setDoc(mapperHelper.getJsonNode(properties));
      resource.addType(ID_LCCN);
    } else {
      resource.addType(ID_UNKNOWN);
    }
    resource.setId(hashService.hash(resource));
  }

  private void adjustWorkToInstanceRelation(Resource resource) {
    for (var iterator = resource.getOutgoingEdges().iterator(); iterator.hasNext(); ) {
      var resourceEdge = iterator.next();
      if (INSTANTIATES.equals(resourceEdge.getPredicate())) {
        var instance = resourceEdge.getTarget();
        var instanceToWorkResourceEdge = new ResourceEdge(instance, resource, INSTANTIATES);
        instance.addOutgoingEdge(instanceToWorkResourceEdge);
        resource.getIncomingEdges().add(instanceToWorkResourceEdge);
        iterator.remove();
        break;
      }
    }
  }
}
