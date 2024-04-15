package org.folio.marc4ld.mapper.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
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

  public static void validateEdge(ResourceEdge edge, PredicateDictionary predicate,
                            List<ResourceTypeDictionary> types, Map<String, String> properties, String expectedLabel) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(predicate.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(predicate.getUri());
    assertThat(edge.getTarget().getId()).isNotNull();
    assertThat(edge.getTarget().getTypes()).containsOnly(types.toArray(new ResourceTypeDictionary[0]));
    assertThat(edge.getTarget().getLabel()).isEqualTo(expectedLabel);
    assertThat(edge.getTarget().getDoc()).hasSize(properties.size());
    properties.forEach((property, propertyValue) -> validateProperty(edge.getTarget(), property, propertyValue));
  }

  public static void validateProperty(Resource resource, String propertyKey, String propertyValue) {
    assertThat(resource.getDoc().has(propertyKey)).isTrue();
    assertThat(resource.getDoc().get(propertyKey).size()).isEqualTo(1);
    assertThat(resource.getDoc().get(propertyKey).get(0).asText()).isEqualTo(propertyValue);
  }
}
