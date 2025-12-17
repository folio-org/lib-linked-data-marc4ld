package org.folio.marc4ld.postprocessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.EXPRESSION_OF;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PLACE;
import static org.folio.ld.dictionary.PropertyDictionary.SUBORDINATE_UNIT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FAMILY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.MEETING;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Marc1xxTo7xxNormalizerIT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void marcShouldContain130And7xxFields() {
    // given
    var expectedMarc = loadResourceAsString("postprocessor/marc_130_7xx.json");
    var instanceWithMultipleCreators = createInstance(
      List.of(createHubResource()),
      List.of(createMeetingResource(), createOrganizationResource(), createPersonResource(), createFamilyResource())
    );

    // when
    var actualMarc = ld2MarcMapper.toMarcJson(instanceWithMultipleCreators);

    // then
    assertThat(actualMarc).isEqualTo(expectedMarc);
  }

  @Test
  void marcShouldContain100And7xxFields() {
    // given
    var expectedMarc = loadResourceAsString("postprocessor/marc_100_7xx.json");
    var instanceWithMultipleCreators = createInstance(
      List.of(),
      List.of(createMeetingResource(), createOrganizationResource(), createPersonResource(), createFamilyResource())
    );

    // when
    var actualMarc = ld2MarcMapper.toMarcJson(instanceWithMultipleCreators);

    // then
    assertThat(actualMarc).isEqualTo(expectedMarc);
  }

  private Resource createInstance(List<Resource> expressionOf, List<Resource> creators) {
    var work = createResource(
      Map.of(),
      Set.of(WORK, BOOKS),
      Map.of(
        CREATOR, creators,
        EXPRESSION_OF, expressionOf
      )
    );

    return createResource(
      Map.of(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }

  private Resource createFamilyResource() {
    return createResource(
      Map.of(
        NAME, List.of("Rinehart family")
      ),
      Set.of(FAMILY),
      Map.of()
    );
  }

  private Resource createPersonResource() {
    return createResource(
      Map.of(
        NAME, List.of("Ludwig, Alexander"),
        DATE, List.of("1880-")
      ),
      Set.of(PERSON),
      Map.of(MAP, List.of(createLccnResource("n2023009647")))
    );
  }

  private Resource createOrganizationResource() {
    return createResource(
      Map.of(
        NAME, List.of("Association for Japanese Literary Studies"),
        SUBORDINATE_UNIT, List.of("Annual Meeting"),
        DATE, List.of("2018"),
        PLACE, List.of("University of California, Berkeley")
      ),
      Set.of(ORGANIZATION),
      Map.of(MAP, List.of(createLccnResource("no2023017580")))
    );
  }

  private Resource createMeetingResource() {
    return createResource(
      Map.of(
        NAME, List.of("International Society for Geomorphometry. Conference"),
        DATE, List.of("2015"),
        PLACE, List.of("Poznań, Poland")
      ),
      Set.of(MEETING),
      Map.of(MAP, List.of(createLccnResource("n2023008934")))
    );
  }

  private Resource createLccnResource(String lccn) {
    return createResource(
      Map.of(
        NAME, List.of(lccn),
        LINK, List.of("http://id.loc.gov/authorities/names/" + lccn)
      ),
      Set.of(IDENTIFIER, ID_LCCN),
      Map.of()
    );
  }

  private Resource createHubResource() {
    var yearOfMarvalsTitle = createResource(
      Map.of(
        MAIN_TITLE, List.of("Year of Marvels. Unstoppable."),
        PART_NAME, List.of("Unstoppable")
      ),
      Set.of(TITLE),
      Map.of()
    );

    return createResource(
      Map.of(LABEL, List.of("Year of Marvels. Unstoppable.")),
      Set.of(HUB),
      Map.of(PredicateDictionary.TITLE, List.of(yearOfMarvalsTitle))
    );
  }
}
