package org.folio.marc4ld.test.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.spring.testing.type.UnitTest;

@UnitTest
public class ResourceEdgeHelper {

  public static List<ResourceEdge> getIncomingEdges(Resource resource, Predicate<ResourceEdge> predicate) {
    return resource
      .getIncomingEdges()
      .stream()
      .filter(predicate)
      .toList();
  }

  public static List<ResourceEdge> getIncomingEdges(ResourceEdge resourceEdge, Predicate<ResourceEdge> predicate) {
    return getIncomingEdges(resourceEdge.getTarget(), predicate);
  }

  public static ResourceEdge getFirstIncomingEdge(ResourceEdge resourceEdge, Predicate<ResourceEdge> predicate) {
    return getIncomingEdges(resourceEdge, predicate)
      .stream()
      .findFirst()
      .orElseThrow();
  }

  public static List<ResourceEdge> getOutgoingEdges(ResourceEdge resourceEdge) {
    return getOutgoingEdges(resourceEdge, re -> true);
  }

  public static List<ResourceEdge> getOutgoingEdges(Resource resource, Predicate<ResourceEdge> predicate) {
    return resource
      .getOutgoingEdges()
      .stream()
      .filter(predicate)
      .toList();
  }

  public static List<ResourceEdge> getOutgoingEdges(ResourceEdge resourceEdge, Predicate<ResourceEdge> predicate) {
    return getOutgoingEdges(resourceEdge.getTarget(), predicate);
  }

  public static ResourceEdge getFirstOutgoingEdge(ResourceEdge resourceEdge, Predicate<ResourceEdge> predicate) {
    return getOutgoingEdges(resourceEdge, predicate)
      .stream()
      .findFirst()
      .orElseThrow();
  }

  public static ResourceEdge getFirstOutgoingEdge(Resource resource, Predicate<ResourceEdge> predicate) {
    return getOutgoingEdges(resource, predicate)
      .stream()
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

  public static Optional<ResourceEdge> getEdge(Resource resource, ResourceTypeDictionary... resourceTypes) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(edge -> Optional.of(edge.getTarget())
        .map(Resource::getTypes)
        .filter(types -> CollectionUtils.containsAll(types, Arrays.asList(resourceTypes)))
        .isPresent())
      .findFirst();
  }

  public static Predicate<ResourceEdge> withPredicateUri(String uri) {
    return re -> re.getPredicate().getUri().equals(uri);
  }
}
