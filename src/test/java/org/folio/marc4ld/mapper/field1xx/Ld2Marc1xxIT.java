package org.folio.marc4ld.mapper.field1xx;

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
class Ld2Marc1xxIT {
  @Autowired
  private Ld2MarcMapperImpl ld2MarcMapper;

  @Test
  void marcShouldContainOnlySingle1xxField() {
    // given
    var expectedMarc = loadResourceAsString("fields/1xx/marc_1xx_7xx.json");
    var instanceWithMultipleCreators = instanceWithMultipleCreators();

    // when
    var actualMarc = ld2MarcMapper.toMarcJson(instanceWithMultipleCreators);

    // then
    assertThat(actualMarc).isEqualTo(expectedMarc);
  }

  private Resource instanceWithMultipleCreators() {
    var yearOfMarvalsTitle = createResource(
      Map.of(
        MAIN_TITLE, List.of("Year of Marvels. Unstoppable."),
        PART_NAME, List.of("Unstoppable")
      ),
      Set.of(TITLE),
      Map.of()
    );

    var yearOfMarvals = createResource(
      Map.of(LABEL, List.of("Year of Marvels. Unstoppable.")),
      Set.of(HUB),
      Map.of(PredicateDictionary.TITLE, List.of(yearOfMarvalsTitle))
    );

    var geometryMeetingLccn = createResource(
      Map.of(
        NAME, List.of("n2023008934"),
        LINK, List.of("http://id.loc.gov/authorities/names/n2023008934")
      ),
      Set.of(IDENTIFIER, ID_LCCN),
      Map.of()
    );

    var geometryMeeting = createResource(
      Map.of(
        NAME, List.of("International Society for Geomorphometry. Conference"),
        DATE, List.of("2015"),
        PLACE, List.of("Poznań, Poland")
      ),
      Set.of(MEETING),
      Map.of(MAP, List.of(geometryMeetingLccn))
    );

    var japaneseLiteraryStudiesLccn = createResource(
      Map.of(
        NAME, List.of("no2023017580"),
        LINK, List.of("http://id.loc.gov/authorities/names/no2023017580")
      ),
      Set.of(IDENTIFIER, ID_LCCN),
      Map.of()
    );

    var japaneseLiteraryStudies = createResource(
      Map.of(
        NAME, List.of("Association for Japanese Literary Studies"),
        SUBORDINATE_UNIT, List.of("Annual Meeting"),
        DATE, List.of("2018"),
        PLACE, List.of("University of California, Berkeley")
      ),
      Set.of(ORGANIZATION),
      Map.of(MAP, List.of(japaneseLiteraryStudiesLccn))
    );

    var alexanderLccn = createResource(
      Map.of(
        NAME, List.of("n2023009647"),
        LINK, List.of("http://id.loc.gov/authorities/names/n2023009647")
      ),
      Set.of(IDENTIFIER, ID_LCCN),
      Map.of()
    );

    var alexandr = createResource(
      Map.of(
        NAME, List.of("Ludwig, Alexander"),
        DATE, List.of("1880-")
      ),
      Set.of(PERSON),
      Map.of(MAP, List.of(alexanderLccn))
    );

    var regnier = createResource(
      Map.of(
        NAME, List.of("Rinehart family")
      ),
      Set.of(FAMILY),
      Map.of()
    );

    var work = createResource(
      Map.of(),
      Set.of(WORK, BOOKS),
      Map.of(
        CREATOR, List.of(geometryMeeting, japaneseLiteraryStudies, alexandr, regnier),
        EXPRESSION_OF, List.of(yearOfMarvals)
      )
    );

    return createResource(
      Map.of(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }
}
