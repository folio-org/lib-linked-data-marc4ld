package org.folio.marc4ld.util;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import lombok.experimental.UtilityClass;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

@UtilityClass
public class JsonUtil {

  public static JsonMapper getJsonMapper() {
    return JsonMapper.builder()
      .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(NON_NULL))
      .enable(SerializationFeature.INDENT_OUTPUT)
      .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .build();
  }
}
