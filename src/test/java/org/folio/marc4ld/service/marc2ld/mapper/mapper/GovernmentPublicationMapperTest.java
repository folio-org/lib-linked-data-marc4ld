package org.folio.marc4ld.service.marc2ld.mapper.mapper;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.TERM;
import static org.folio.marc4ld.mapper.test.TestUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.dto.MarcData;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.MarcFactory;

@UnitTest
class GovernmentPublicationMapperTest {

  private static final String LINK_PREFIX = "http://id.loc.gov/vocabulary/mgovtpubtype/";
  private GovernmentPublicationMapper mapper;
  private final MarcFactory factory = MarcFactory.newInstance();

  @BeforeEach
  void setup() {
    mapper = new GovernmentPublicationMapper(new MapperHelper(OBJECT_MAPPER));
  }

  @ParameterizedTest
  @CsvSource(value = {
    "a , a, Autonomous",
    "c , c, Multilocal",
    "f , f, Federal",
    "i , i, International Intergovernmental",
    "l , l, Local",
    "m , m, Multistate",
    "o , g, Government",
    "s , s, State"
  })
  void shouldMapGovernmentPublication(String charInMarc, String expectedLinkSuffix, String expectedTerm) {
    // given
    var resource = new Resource().setDoc(OBJECT_MAPPER.convertValue(Map.of(), JsonNode.class));
    ControlField controlField = factory.newControlField("008", repeat(' ', 28) + charInMarc);

    // when
    mapper.map(new MarcData(factory.newDataField(), List.of(controlField)), resource);

    // then
    var properties = OBJECT_MAPPER.convertValue(resource.getDoc(),
      new TypeReference<HashMap<String, List<String>>>() {
      });
    var actualLink = properties.get(LINK.getValue()).get(0);
    var actualTerm = properties.get(TERM.getValue()).get(0);
    assertThat(actualLink).isEqualTo(LINK_PREFIX + expectedLinkSuffix);
    assertThat(actualTerm).isEqualTo(expectedTerm);
  }

  @Test
  void shouldGracefullyIgnoreControlFieldLessThan28Characters() {
    // given
    var controlField = factory.newControlField("008", "   ");

    // expect
    assertThatCode(() -> mapper.map(new MarcData(factory.newDataField(), List.of(controlField)), new Resource()))
      .doesNotThrowAnyException();
  }

  @Test
  void shouldGracefullyIgnoreControlFieldWithNullValue() {
    // given
    var controlField = factory.newControlField("008", null);

    // expect
    assertThatCode(() -> mapper.map(new MarcData(factory.newDataField(), List.of(controlField)), new Resource()))
      .doesNotThrowAnyException();
  }

  @Test
  void shouldNotThrowExceptionIfControlField008isNotPresent() {
    // given
    List<ControlField> emptyControlFields = List.of();

    // expect
    assertThatCode(() -> mapper.map(new MarcData(factory.newDataField(), emptyControlFields), new Resource()))
      .doesNotThrowAnyException();
  }
}
