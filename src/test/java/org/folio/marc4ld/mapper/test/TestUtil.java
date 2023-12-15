package org.folio.marc4ld.mapper.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ResourceLoader;

@UtilityClass
public class TestUtil {

  public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @SneakyThrows
  public static String loadResourceAsString(String resourceName) {
    var classLoader = ResourceLoader.class.getClassLoader();
    var is = Objects.requireNonNull(classLoader.getResourceAsStream(resourceName));
    return IOUtils.toString(is, StandardCharsets.UTF_8);
  }
}
