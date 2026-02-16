package org.folio.marc4ld.mapper.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.springframework.core.io.ResourceLoader;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@UtilityClass
public class TestUtil {

  public static final JsonMapper JSON_MAPPER = new JsonMapper();

  @SneakyThrows
  public static String loadResourceAsString(String resourceName) {
    var classLoader = ResourceLoader.class.getClassLoader();
    var is = Objects.requireNonNull(classLoader.getResourceAsStream(resourceName));
    return IOUtils.toString(is, StandardCharsets.UTF_8);
  }

  public static void validateEdge(ResourceEdge edge, PredicateDictionary predicate,
                                  List<ResourceTypeDictionary> types,
                                  Map<String, List<String>> properties,
                                  String expectedLabel) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(predicate.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(predicate.getUri());
    validateResource(edge.getTarget(), types, properties, expectedLabel);
  }

  public static void validateEdgeWithSource(ResourceEdge edge, PredicateDictionary predicate,
                                  List<ResourceTypeDictionary> types,
                                  Map<String, List<String>> properties,
                                  String expectedLabel) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(predicate.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(predicate.getUri());
    validateResource(edge.getSource(), types, properties, expectedLabel);
  }

  public static void validateResource(Resource resource,
                                      List<ResourceTypeDictionary> types,
                                      Map<String, List<String>> properties,
                                      String expectedLabel) {
    assertThat(resource.getId()).isNotNull();
    assertThat(resource.getTypes()).containsOnly(types.toArray(new ResourceTypeDictionary[0]));
    assertThat(resource.getLabel()).isEqualTo(expectedLabel);
    assertThat(resource.getDoc()).hasSize(properties.size());
    properties.forEach((property, propertyValues) -> validateProperty(resource, property, propertyValues));
  }

  public static void validateProperty(Resource resource, String propertyKey, List<String> propertyValues) {
    assertThat(resource.getDoc().has(propertyKey))
      .as("Property %s is not found in resource", propertyKey)
      .isTrue();
    var values = resource.getDoc().get(propertyKey);
    var actualValues = StreamSupport.stream(values.spliterator(), false)
      .map(JsonNode::asString)
      .toList();
    assertThat(actualValues)
      .containsOnlyOnceElementsOf(propertyValues);
  }

  public static void validateCurrentStatus(Resource status) {
    validateResource(
      status,
      List.of(ResourceTypeDictionary.STATUS),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("current"),
        "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mstatus/current")
      ),
      "current"
    );
  }

  public static void validateCancelledStatus(Resource status) {
    validateResource(
      status,
      List.of(ResourceTypeDictionary.STATUS),
      Map.of(
        "http://bibfra.me/vocab/lite/label", List.of("canceled or invalid"),
        "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mstatus/cancinv")
      ),
      "canceled or invalid"
    );
  }
}
