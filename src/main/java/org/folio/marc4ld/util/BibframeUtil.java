package org.folio.marc4ld.util;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.springframework.util.CollectionUtils;

@UtilityClass
public class BibframeUtil {

  public static String getFirst(Collection<String> values) {
    return values.stream()
      .filter(StringUtils::isNotBlank)
      .findFirst()
      .orElse(EMPTY);
  }

  public static boolean isEmpty(Resource r) {
    return isEmptyDoc(r.getDoc())
      && CollectionUtils.isEmpty(r.getOutgoingEdges());
  }

  public static boolean isNotEmpty(Resource r) {
    return !isEmpty(r);
  }

  public static Optional<String> getPropertyValue(Resource resource, String property) {
    return Optional.of(resource)
      .map(Resource::getDoc)
      .map(doc -> doc.get(property))
      .map(node -> node.get(0))
      .map(JsonNode::asText);
  }

  public static List<String> getPropertyValues(Resource resource, String property,
                                               Function<JsonNode, List<String>> propertiesConversionFunction) {
    return Optional.of(resource)
      .map(Resource::getDoc)
      .map(doc -> doc.get(property))
      .map(propertiesConversionFunction)
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

  private static boolean isEmptyDoc(JsonNode doc) {
    return isNull(doc) || doc.isEmpty();
  }
}
