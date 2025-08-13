package org.folio.marc4ld.mapper.field040;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ADMIN_METADATA;
import static org.folio.ld.dictionary.PredicateDictionary.CATALOGING_LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PropertyDictionary.CATALOGING_AGENCY;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.CONTROL_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.CREATED_DATE;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.MODIFYING_AGENCY;
import static org.folio.ld.dictionary.PropertyDictionary.TRANSCRIBING_AGENCY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ANNOTATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.LANGUAGE_CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2Marc040IT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMapAdminMetadataToField040() {
    // given
    var expectedMarc = loadResourceAsString("fields/040/marc_040.jsonl");
    var instance = createInstanceWithAdminMetadata();

    // when
    var result = ld2MarcMapper.toMarcJson(instance);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithAdminMetadata() {

    var work = createResource(
      Map.of(
      ),
      Set.of(WORK, BOOKS),
      Map.of()
    );

    var language = createResource(
      Map.of(
        CODE, List.of("fre"),
        LINK, List.of("http://id.loc.gov/vocabulary/languages/fre")
      ),
      Set.of(LANGUAGE_CATEGORY),
      Map.of()
    );

    var adminMetadata = createResource(
      Map.of(
        CATALOGING_AGENCY, List.of("DLC"),
        TRANSCRIBING_AGENCY, List.of("CtY"),
        MODIFYING_AGENCY, List.of("MH", "NU"),
        CONTROL_NUMBER, List.of("123456789"),
        CREATED_DATE, List.of("010815")
      ),
      Set.of(ANNOTATION),
      Map.of(CATALOGING_LANGUAGE, List.of(language))
    );

    return createResource(
      Map.of(),
      Set.of(INSTANCE),
      Map.of(
        ADMIN_METADATA, List.of(adminMetadata),
        INSTANTIATES, List.of(work)
      )
    );
  }
}
