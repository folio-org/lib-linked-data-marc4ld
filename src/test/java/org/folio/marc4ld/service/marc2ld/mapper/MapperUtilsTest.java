package org.folio.marc4ld.service.marc2ld.mapper;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.JSON_MAPPER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.model.Resource;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.marc4j.marc.MarcFactory;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;

@UnitTest
class MapperUtilsTest {
  private final MarcFactory factory = MarcFactory.newInstance();
  private final MapperHelper mapperHelper = new MapperHelper();

  @Test
  void shouldReturnControlFieldWhenMatchingRecordFound() {
    // given
    var cf007Length10 = factory.newControlField("007", repeat('a', 10));
    var cf008Length10 = factory.newControlField("008", repeat('a', 10));
    var cf008Length1 = factory.newControlField("008", repeat('a', 1));

    // when
    var resultControlField = mapperHelper.getControlField(
      List.of(cf007Length10, cf008Length10, cf008Length1),
      cf008Length10.getTag(),
      cf008Length10.getData().length()
    );

    // then
    assertThat(resultControlField).contains(cf008Length10);
  }

  @Test
  void shouldReturnEmptyWhenMatchingRecordNotFound() {
    // given
    var cf008Length10 = factory.newControlField("008", repeat('a', 10));

    // when
    var resultControlField = mapperHelper.getControlField(
      List.of(cf008Length10), cf008Length10.getTag(), cf008Length10.getData().length() + 1);

    // then
    assertThat(resultControlField).isEmpty();
  }

  @Test
  void shouldAddPropertiesToResource() {
    // given
    var existingProperties = Map.of(
      "colors", List.of("red", "blue"),
      "fruits", List.of("apples", "bananas")
    );

    var resource = new Resource().setDoc(JSON_MAPPER.convertValue(existingProperties, JsonNode.class));

    var additionalProperties = Map.of(
      "colors", List.of("green"),
      "pets", List.of("dogs", "cats")
    );

    // when
    mapperHelper.addPropertiesToResource(resource, additionalProperties);

    // then
    var actualProperties = JSON_MAPPER.convertValue(resource.getDoc(),
      new TypeReference<HashMap<String, List<String>>>() {
      });
    assertThat(actualProperties)
      .containsAllEntriesOf(Map.of(
        "colors", List.of("red", "blue", "green"),
        "fruits", List.of("apples", "bananas"),
        "pets", List.of("dogs", "cats")
      ));
  }

  @Test
  void shouldAddPropertiesToResource_whenExistingPropertiesIsNull() {
    // given
    var resource = new Resource().setDoc(null);
    var additionalProperties = Map.of(
      "colors", List.of("green"),
      "pets", List.of("dogs", "cats")
    );

    // when
    mapperHelper.addPropertiesToResource(resource, additionalProperties);

    // then
    var actualProperties = JSON_MAPPER.convertValue(resource.getDoc(),
      new TypeReference<HashMap<String, List<String>>>() {
      });
    assertThat(actualProperties).isEqualTo(additionalProperties);
  }
}
