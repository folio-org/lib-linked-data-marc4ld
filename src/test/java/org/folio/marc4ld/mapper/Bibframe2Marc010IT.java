package org.folio.marc4ld.mapper;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.lccn;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Bibframe2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Bibframe2Marc010IT {

  @Autowired
  private Bibframe2MarcMapperImpl bibframe2MarcMapper;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void should_map_only_one_subfield_a() {
    // given
    var expectedMarc = loadResourceAsString("fields/010/marc_010_a.jsonl");
    var resource = createInstanceWithLccn(
      createResource(
        Map.of(NAME, List.of("2019493855")),
        Set.of(IDENTIFIER, ID_LCCN),
        emptyMap()
      ).setLabel("2019493855"),
      lccn("2019493854", "current"),
      lccn("11111111", "other")
    );

    // when
    var result = bibframe2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result)
      .isEqualTo(expectedMarc);
  }

  @Test
  void should_map_all_subfields_z() {
    // given
    var expectedMarc = loadResourceAsString("fields/010/marc_010_z.jsonl");
    var resource = createInstanceWithLccn(
      lccn("88888888", "canceled or invalid"),
      lccn("99999999", "canceled or invalid"),
      lccn("11111111", "other")
    );

    // when
    var result = bibframe2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result)
      .isEqualTo(expectedMarc);
  }

  @Test
  void should_ignore_lccn_with_invalid_status() throws JsonProcessingException {
    // given
    var resource = createInstanceWithLccn(
      lccn("11111111", "invalid_status")
    );

    // when
    var result = bibframe2MarcMapper.toMarcJson(resource);

    // then
    Map<String, Object> resultAsMap = objectMapper.readValue(result, new TypeReference<>() {
    });
    assertThat((List) resultAsMap.get("fields")).isEqualTo(List.of(
      Map.of("008", "                                       ")
    ));
  }

  private Resource createInstanceWithLccn(Resource... lccns) {
    return createResource(
      emptyMap(),
      Set.of(INSTANCE),
      Map.of(MAP, List.of(lccns))
    );
  }
}
