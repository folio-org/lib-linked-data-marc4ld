package org.folio.marc4ld.util;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.model.Resource;

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

  public static boolean isNotEmptyResource(Resource r) {
    return nonNull(r.getDoc()) && !r.getDoc().isEmpty() || !isEmpty(r.getOutgoingEdges());
  }

}
