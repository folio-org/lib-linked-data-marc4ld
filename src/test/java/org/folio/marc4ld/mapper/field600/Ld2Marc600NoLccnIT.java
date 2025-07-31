package org.folio.marc4ld.mapper.field600;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;

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

class Ld2Marc600NoLccnIT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void shouldMapField600() {
    // given
    var resource = createInstanceWithSubject();
    var expectedMarc = """
      {
        "leader" : "00099nam a2200049uc 4500",
        "fields" : [ {
          "008" : "                                       "
        }, {
          "600" : {
            "subfields" : [ {
              "a" : "name"
            } ],
            "ind1" : " ",
            "ind2" : " "
          }
        } ]
      }""";

    // when
    var actualMarc = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(actualMarc).isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithSubject() {
    var person = createResource(
      Map.of(
        NAME, List.of("name")
      ),
      Set.of(PERSON),
      Map.of()
    );

    var concept = createResource(
      Map.ofEntries(
        Map.entry(NAME, List.of("name"))
      ),
      Set.of(PERSON, CONCEPT),
      Map.of(FOCUS, List.of(person))
    );
    var work = createResource(
      Map.of(),
      Set.of(WORK, BOOKS),
      Map.of(SUBJECT, List.of(concept))
    );
    return createResource(
      Map.of(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }
}
