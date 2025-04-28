package org.folio.marc4ld.util;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.StreamSupport.stream;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.folio.ld.dictionary.PredicateDictionary.ADMIN_METADATA;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.springframework.util.CollectionUtils;

@UtilityClass
public class LdUtil {

  public static String getFirst(Collection<String> values) {
    return values.stream()
      .filter(StringUtils::isNotBlank)
      .findFirst()
      .orElse(EMPTY);
  }

  public static boolean isEmpty(Resource resource) {
    return isEmptyDoc(resource.getDoc())
      && CollectionUtils.isEmpty(resource.getOutgoingEdges());
  }

  public static boolean isNotEmpty(Resource resource) {
    return !isEmpty(resource);
  }

  public static boolean isInstance(Resource resource) {
    return nonNull(resource) && resource.getTypes().equals(Set.of(INSTANCE));
  }

  public static Optional<String> getPropertyValue(Resource resource, String property) {
    return Optional.of(resource)
      .map(Resource::getDoc)
      .map(doc -> doc.get(property))
      .map(node -> node.get(0))
      .map(JsonNode::asText);
  }

  public static List<String> getPropertyValues(Resource resource, String property) {
    return Optional.of(resource)
      .map(Resource::getDoc)
      .map(doc -> doc.get(property))
      .map(node -> stream(node.spliterator(), false).map(JsonNode::asText).toList())
      .orElse(List.of());
  }

  public static Optional<Resource> getWork(Resource resource) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> INSTANTIATES == resourceEdge.getPredicate())
      .map(ResourceEdge::getTarget)
      .findFirst();
  }

  public static List<ResourceEdge> getOutgoingEdges(Resource resource, PredicateDictionary predicate) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> predicate == resourceEdge.getPredicate())
      .toList();
  }

  public static Optional<Resource> getAdminMetadata(Resource resource) {
    return resource.getOutgoingEdges().stream()
      .filter(re -> re.getPredicate().getUri().equals(ADMIN_METADATA.getUri()))
      .findFirst()
      .map(ResourceEdge::getTarget);
  }

  public static void reverseFirstEdgeWithPredicate(Resource resource, PredicateDictionary predicate) {
    if (isNull(predicate)) {
      return;
    }
    for (var iterator = resource.getOutgoingEdges().iterator(); iterator.hasNext(); ) {
      var resourceEdge = iterator.next();
      if (predicate.equals(resourceEdge.getPredicate())) {
        var target = resourceEdge.getTarget();
        var newEdge = new ResourceEdge(target, resource, predicate);
        target.addOutgoingEdge(newEdge);
        resource.getIncomingEdges().add(newEdge);
        iterator.remove();
        break;
      }
    }
  }

  private static boolean isEmptyDoc(JsonNode doc) {
    return isNull(doc) || doc.isEmpty();
  }
}
