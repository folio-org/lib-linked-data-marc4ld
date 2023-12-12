package org.folio.marc2ld.util;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL_RDF;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.folio.marc2ld.model.Resource;

@UtilityClass
public class BibframeUtil {

  public static String getFirstValue(Supplier<List<String>> valuesSupplier) {
    if (isNull(valuesSupplier)) {
      return EMPTY;
    }
    var values = valuesSupplier.get();
    if (isNotEmpty(values)) {
      return values.stream()
        .filter(StringUtils::isNotBlank)
        .findFirst()
        .orElse(EMPTY);
    }
    return EMPTY;
  }

  public static Long hash(Resource resource, ObjectMapper mapper) {
    var serialized = resourceToJson(mapper, resource);
    return Hashing.murmur3_32_fixed().hashString(serialized.toString(), StandardCharsets.UTF_8).padToLong();
  }

  public static JsonNode resourceToJson(ObjectMapper mapper, Resource res) {
    ObjectNode node;
    if (nonNull(res.getDoc()) && !res.getDoc().isEmpty()) {
      node = res.getDoc().deepCopy();
    } else {
      node = mapper.createObjectNode();
    }
    node.put(LABEL_RDF.getValue(), res.getLabel());
    if (nonNull(res.getTypes())) {
      node.put("type", res.getTypes().iterator().next().getHash());
    }
    res.getOutgoingEdges().forEach(edge -> {
      var predicate = edge.getPredicate().getUri();
      if (!node.has(predicate)) {
        node.set(predicate, mapper.createArrayNode());
      }
      ((ArrayNode) node.get(predicate)).add(resourceToJson(mapper, edge.getTarget()));
    });
    return node;
  }

  public static boolean isNotEmptyResource(Resource r) {
    return nonNull(r.getDoc()) && !r.getDoc().isEmpty() || !isEmpty(r.getOutgoingEdges());
  }

}
