package org.folio.marc4ld.mapper.field020;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.QUALIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_ISBN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.isbn;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapper;
import org.folio.marc4ld.service.ld2marc.impl.Ld2MarcUnitedMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2Marc020IT {

  @Autowired
  @Qualifier(Ld2MarcUnitedMapper.NAME)
  private Ld2MarcMapper ld2MarcMapper;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void should_map_each_isbn_edge() {
    // given
    var expectedMarc = loadResourceAsString("fields/020/marc_020.jsonl");
    var resource = createInstanceWithIsbn(
      createResource(
        Map.of(
          NAME, List.of("1000"),
          QUALIFIER, List.of("hardcover")
        ),
        Set.of(IDENTIFIER, ID_ISBN),
        emptyMap()
      ).setLabel("1000"),
      isbn(
        Map.of(
          NAME, List.of("1001"),
          QUALIFIER, List.of("paperback")
        ), "current", "1001"),
      isbn(
        Map.of(
          NAME, List.of("1002"),
          QUALIFIER, List.of("black leather")
        ), "canceled or invalid", "1002"),
      isbn(
        Map.of(
          NAME, List.of("1003")
        ), "canceled or invalid", "1003")
    );

    // when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithIsbn(Resource... isbns) {
    return createResource(
      emptyMap(),
      Set.of(INSTANCE),
      Map.of(MAP, List.of(isbns))
    );
  }
}
