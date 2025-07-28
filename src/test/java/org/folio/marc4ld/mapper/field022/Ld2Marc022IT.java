package org.folio.marc4ld.mapper.field022;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_ISSN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
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
class Ld2Marc022IT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMapIssnTo022() {
    //
    var expectedMarc = loadResourceAsString("fields/022/marc_022.jsonl");
    var instance = createInstanceWithIssn();

    // when
    var result = ld2MarcMapper.toMarcJson(instance);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithIssn() {
    var currentIssn = createIssn("0046-1111", status("current"));
    var incorrectIssn = createIssn("0046-2222", status("incorrect"));
    var cancelledIssn = createIssn("0046-3333", status("canceled or invalid"));

    return createResource(
      Map.of(),
      Set.of(INSTANCE),
      Map.of(
        MAP, List.of(currentIssn, incorrectIssn, cancelledIssn)
      )
    );
  }

  private Resource createIssn(String issn, Resource status) {
    return createResource(
      Map.of(NAME, List.of(issn)),
      Set.of(IDENTIFIER, ID_ISSN),
      Map.of(STATUS, List.of(status))
    ).setLabel(issn);
  }
}
