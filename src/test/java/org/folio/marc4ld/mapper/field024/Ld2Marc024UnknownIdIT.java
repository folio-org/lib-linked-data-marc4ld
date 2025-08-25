package org.folio.marc4ld.mapper.field024;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.QUALIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_UNKNOWN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createWorkBook;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.status;
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
class Ld2Marc024UnknownIdIT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMapUnknownIdTo024() {
    // given
    var expectedMarc = loadResourceAsString("fields/024/marc_024_unknown_id.jsonl");
    var instance = createInstanceWithUnknownId();

    // when
    var result = ld2MarcMapper.toMarcJson(instance);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithUnknownId() {
    var currentIan = createUnknownId("UNKNOWN-ID-01", List.of("q1", "q2"), status("current"));
    var cancelledIan = createUnknownId("UNKNOWN-ID-02", List.of("q3"), status("canceled or invalid"));

    return createResource(
      Map.of(),
      Set.of(INSTANCE),
      Map.of(
        MAP, List.of(currentIan, cancelledIan),
        INSTANTIATES, List.of(createWorkBook())
      )
    );
  }

  private Resource createUnknownId(String id, List<String> qualifiers, Resource status) {
    return createResource(
      Map.of(
        NAME, List.of(id),
        QUALIFIER, qualifiers
      ),
      Set.of(IDENTIFIER, ID_UNKNOWN),
      Map.of(STATUS, List.of(status))
    ).setLabel(id);
  }
}
