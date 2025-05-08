package org.folio.marc4ld.mapper.field340;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.MonographTestUtil;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class LdToMarc340IT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMapToField340FromCategory() {
    // given
    var expectedMarc = loadResourceAsString("fields/340/marc_340_standard_book_format.jsonl");
    var instance = createInstanceWithBookFormatCategory();

    // when
    var result = ld2MarcMapper.toMarcJson(instance);

    // then
    assertThat(result)
      .isEqualTo(expectedMarc);
  }

  @Test
  void shouldMapToField340FromString() {
    // given
    var expectedMarc = loadResourceAsString("fields/340/marc_340_non_standard_book_format.jsonl");
    var instance = createInstanceWithBookFormatString();

    // when
    var result = ld2MarcMapper.toMarcJson(instance);

    // then
    assertThat(result)
      .isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithBookFormatCategory() {
    var categorySet = MonographTestUtil.createResource(
      Map.of(
        PropertyDictionary.LINK, List.of("http://id.loc.gov/vocabulary/bookformat"),
        PropertyDictionary.LABEL, List.of("Book Format")
      ),
      Set.of(CATEGORY_SET),
      Collections.emptyMap()
    ).setLabel("Book Format");

    var bookFormat = MonographTestUtil.createResource(
      Map.of(
        PropertyDictionary.CODE, List.of("4to"),
        PropertyDictionary.LINK, List.of("http://id.loc.gov/vocabulary/bookformat/4to"),
        PropertyDictionary.TERM, List.of("4to."),
        PropertyDictionary.SOURCE, List.of("rdabf")
      ),
      Set.of(CATEGORY),
      Map.of(PredicateDictionary.IS_DEFINED_BY, List.of(categorySet))
    ).setLabel("4to");

    return MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      Map.of(PredicateDictionary.BOOK_FORMAT, List.of(bookFormat))
    );
  }

  private Resource createInstanceWithBookFormatString() {
    return MonographTestUtil.createResource(
      Map.of(PropertyDictionary.BOOK_FORMAT, List.of("book format label.")),
      Set.of(INSTANCE),
      Collections.emptyMap()
    );
  }
}
