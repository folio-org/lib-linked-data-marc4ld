package org.folio.marc4ld.test.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;

public class ResourceEdgeHelper {

  public static List<ResourceEdge> getOutgoingEdges(ResourceEdge resourceEdge) {
    return resourceEdge.getTarget()
      .getOutgoingEdges()
      .stream()
      .toList();
  }

  public static List<ResourceEdge> getOutgoingEdges(ResourceEdge resourceEdge, Predicate<ResourceEdge> predicate) {
    return resourceEdge.getTarget()
      .getOutgoingEdges()
      .stream()
      .filter(predicate)
      .toList();
  }

  public static ResourceEdge getFirstOutgoingEdge(ResourceEdge resourceEdge, Predicate<ResourceEdge> predicate) {
    return getOutgoingEdges(resourceEdge)
      .stream()
      .filter(predicate)
      .findFirst()
      .orElseThrow();
  }

  public static ResourceEdge getFirstOutgoingEdge(Resource resource, Predicate<ResourceEdge> predicate) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(predicate)
      .findFirst()
      .orElseThrow();
  }

  public static ResourceEdge getWorkEdge(Resource resource) {
    return getFirstOutgoingEdge(resource, withPredicateUri("http://bibfra.me/vocab/lite/instantiates"));
  }

  public static List<ResourceEdge> getEdges(Resource resource, ResourceTypeDictionary... resourceTypes) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(edge -> Optional.of(edge.getTarget())
        .map(Resource::getTypes)
        .filter(types -> CollectionUtils.containsAll(types, Arrays.asList(resourceTypes)))
        .isPresent())
      .toList();
  }

  public static Predicate<ResourceEdge> withPredicateUri(String uri) {
    return re -> re.getPredicate().getUri().equals(uri);
  }
}
