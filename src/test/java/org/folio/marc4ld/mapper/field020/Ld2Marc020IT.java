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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2Marc020IT {

  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @ParameterizedTest
  @MethodSource("isbnMappingArguments")
  void shouldMapIsbnTo020(Resource isbn, String expectedMarcFile) {
    // given
    var expectedMarc = loadResourceAsString(expectedMarcFile);
    var instance = createInstanceWithIsbn(isbn);

    // when
    var result = ld2MarcMapper.toMarcJson(instance);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private static Stream<Arguments> isbnMappingArguments() {
    return Stream.of(
      Arguments.of(
        isbn(Map.of(NAME, List.of("1000"), QUALIFIER, List.of("hardcover")), "current", "1000"),
        "fields/020/marc_020_current_with_qualifier.jsonl"
      ),
      Arguments.of(
        isbn(Map.of(NAME, List.of("1000")), "current", "1000"),
        "fields/020/marc_020_current_without_qualifier.jsonl"
      ),
      Arguments.of(
        createResource(
          Map.of(NAME, List.of("1000"), QUALIFIER, List.of("hardcover")),
          Set.of(IDENTIFIER, ID_ISBN),
          emptyMap()
        ).setLabel("1000"),
        "fields/020/marc_020_no_status_with_qualifier.jsonl"
      ),
      Arguments.of(
        isbn(Map.of(NAME, List.of("1002"), QUALIFIER, List.of("black leather")), "canceled or invalid", "1002"),
        "fields/020/marc_020_cancinv_with_qualifier.jsonl"
      ),
      Arguments.of(
        isbn(Map.of(NAME, List.of("1003")), "canceled or invalid", "1003"),
        "fields/020/marc_020_cancinv_without_qualifier.jsonl"
      ),
      Arguments.of(
        isbn(Map.of(QUALIFIER, List.of("hardcover")), "current", ""),
        "fields/020/marc_020_qualifier_only.jsonl"
      ),
      Arguments.of(
        isbn(Map.of(NAME, List.of("1005"), QUALIFIER, List.of("paperback")), "former", "1005"),
        "fields/020/marc_020_no_field.jsonl"
      )
    );
  }

  private Resource createInstanceWithIsbn(Resource isbn) {
    return createResource(
      emptyMap(),
      Set.of(INSTANCE),
      Map.of(MAP, List.of(isbn))
    );
  }
}
