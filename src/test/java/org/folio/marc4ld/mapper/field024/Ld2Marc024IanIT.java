package org.folio.marc4ld.mapper.field024;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.QUALIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_IAN;
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
class Ld2Marc024IanIT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMapIanTo024() {
    // given
    var expectedMarc = loadResourceAsString("fields/024/marc_024_ian.jsonl");
    var instance = createInstanceWithIan();

    // when
    var result = ld2MarcMapper.toMarcJson(instance);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithIan() {
    var currentIan = createIan("IAN-01", List.of("q1", "q2"), status("current"));
    var cancelledIan = createIan("IAN-02", List.of("q3"), status("canceled or invalid"));

    return createResource(
      Map.of(),
      Set.of(INSTANCE),
      Map.of(
        MAP, List.of(currentIan, cancelledIan),
        INSTANTIATES, List.of(createWorkBook())
      )
    );
  }

  private Resource createIan(String ian, List<String> qualifiers, Resource status) {
    return createResource(
      Map.of(
        NAME, List.of(ian),
        QUALIFIER, qualifiers
      ),
      Set.of(IDENTIFIER, ID_IAN),
      Map.of(STATUS, List.of(status))
    ).setLabel(ian);
  }
}
