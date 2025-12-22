package org.folio.marc4ld.mapper.field26x;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.PROVIDER_PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Marc2LdInvalidPlaceCodeIT extends Marc2LdTestBase {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @ParameterizedTest
  @CsvSource(value = {
    "260, ///, http://bibfra.me/vocab/library/publication",
    "261, zm, http://bibfra.me/vocab/library/manufacture",
    "262, --, http://bibfra.me/vocab/library/publication",
    "264, |||, http://bibfra.me/vocab/library/production"
  })
  void shouldReplaceInvalidPlaceCodeWithUnknownPlace(String marcTag, String invalidPlaceCode, String predicate) {
    // given
    var marc = """
     {
        "leader" : "00156nam a2200049uc 4500",
        "fields" : [ {
          "008" : "       2009    %PLACE_CODE%                     "
        }, {
          "%MARC_TAG%" : {
            "subfields" : [ { "a" : "subfield a value" } ],
            "ind1" : " ",
            "ind2" : "0"
          }
        } ]
      }"""
      .replace("%MARC_TAG%", marcTag)
      .replace("%PLACE_CODE%", invalidPlaceCode);

    // when
    var result = marcBibToResource(marc);

    // then
    assertThat(result)
      .extracting(r -> getFirstOutgoingEdge(result, withPredicateUri(predicate)))
      .extracting(e -> getFirstOutgoingEdge(e, withPredicateUri("http://bibfra.me/vocab/lite/providerPlace")))
      .satisfies(e -> validateEdge(e, PROVIDER_PLACE, List.of(PLACE),
        Map.of(
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/countries/xx"),
          "http://bibfra.me/vocab/library/code", List.of("xx"),
          "http://bibfra.me/vocab/lite/label", List.of("No place, unknown, or undetermined"),
          "http://bibfra.me/vocab/lite/name", List.of("No place, unknown, or undetermined")
        ),
        "No place, unknown, or undetermined"));
  }
}
