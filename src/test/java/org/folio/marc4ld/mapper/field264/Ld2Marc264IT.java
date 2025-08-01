package org.folio.marc4ld.mapper.field264;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PROVIDER_EVENT;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createWorkBook;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2Marc264IT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @ParameterizedTest
  @CsvSource(value = {
    "PE_PRODUCTION , fields/264/marc_264_Ind2_equals0.jsonl",
    "PE_PUBLICATION , fields/264/marc_264_Ind2_equals1.jsonl",
    "PE_DISTRIBUTION , fields/264/marc_264_Ind2_equals2.jsonl",
    "PE_MANUFACTURE , fields/264/marc_264_Ind2_equals3.jsonl",
  })
  void shouldMapField264(PredicateDictionary predicate, String expectedMarcFile) {
    // given
    var expectedMarc = loadResourceAsString(expectedMarcFile);
    var resource = createInstanceWithProvisionActivity(predicate);

    //when
    var result = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(result).isEqualTo(expectedMarc);
  }

  private Resource createInstanceWithProvisionActivity(PredicateDictionary activityPredicate) {
    var activityPlace = MonographTestUtil.createResource(
      Map.of(
        PropertyDictionary.NAME, List.of("New York"),
        PropertyDictionary.CODE, List.of("nyu"),
        PropertyDictionary.LINK, List.of("http://id.loc.gov/vocabulary/countries/nyu")
      ),
      Set.of(PLACE),
      Collections.emptyMap()
    ).setLabel("New York");

    var provisionActivity = MonographTestUtil.createResource(
      Map.of(
        PropertyDictionary.NAME, List.of("Name of provision activity"),
        PropertyDictionary.SIMPLE_PLACE, List.of("Place of provision activity"),
        PropertyDictionary.DATE, List.of("2010"),
        PropertyDictionary.PROVIDER_DATE, List.of("2009")
      ),
      Set.of(PROVIDER_EVENT),
      Map.of(PredicateDictionary.PROVIDER_PLACE, List.of(activityPlace))
    ).setLabel("Publisher name");

    return MonographTestUtil.createResource(
      Collections.emptyMap(),
      Set.of(INSTANCE),
      Map.of(
        activityPredicate, List.of(provisionActivity),
        INSTANTIATES, List.of(createWorkBook())
      )
    );
  }
}
