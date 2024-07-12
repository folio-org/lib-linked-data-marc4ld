package org.folio.marc4ld.util;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.model.Resource;
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
    return resource.getDoc().get(property) != null
      ? Optional.of(resource.getDoc().get(property).get(0).asText())
      : Optional.empty();
  }

  public static List<String> getPropertyValues(Resource resource, String property,
                                               Function<JsonNode, List<String>> propertiesConversionFunction) {
    return Optional.ofNullable(resource.getDoc().get(property))
      .map(propertiesConversionFunction)
      .orElse(List.of());
  }

  private static boolean isEmptyDoc(JsonNode doc) {
    return isNull(doc) || doc.isEmpty();
  }
}
