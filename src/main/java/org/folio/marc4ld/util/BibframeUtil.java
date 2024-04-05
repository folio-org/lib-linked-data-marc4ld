package org.folio.marc4ld.util;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
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

  private static boolean isEmptyDoc(JsonNode doc) {
    return isNull(doc) || doc.isEmpty();
  }
}
