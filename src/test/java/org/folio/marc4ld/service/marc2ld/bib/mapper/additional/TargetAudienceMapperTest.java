package org.folio.marc4ld.service.marc2ld.bib.mapper.additional;

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
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.MarcFactory;
import org.mockito.Mockito;

@UnitTest
class TargetAudienceMapperTest {

  private static final String LINK_PREFIX = "http://id.loc.gov/vocabulary/maudience/";

  private TargetAudienceMapper mapper;
  private final MarcFactory factory = MarcFactory.newInstance();

  @BeforeEach
  void setup() {
    FingerprintHashService hashService = Mockito.mock(FingerprintHashService.class);
    mapper = new TargetAudienceMapper(hashService, new MapperHelper(OBJECT_MAPPER));
  }

  @ParameterizedTest
  @CsvSource(value = {
    "a , pre, Preschool",
    "b , pri, Primary",
    "c , pad, Pre-adolescent",
    "d , ado, Adolescent",
    "e , adu, Adult",
    "f , spe, Specialized",
    "g , gen, General",
    "j , juv, Juvenile"
  })
  void shouldMapTargetAudience(String charInMarc, String expectedLinkSuffix, String expectedTerm) {
    // given
    var resource = new Resource().setDoc(OBJECT_MAPPER.convertValue(Map.of(), JsonNode.class));
    ControlField controlField = factory.newControlField("008", repeat(' ', 22) + charInMarc);

    // when
    mapper.map(new MarcData(factory.newDataField(), List.of(controlField)), resource);

    // then
    var properties = OBJECT_MAPPER.convertValue(resource.getDoc(),
      new TypeReference<HashMap<String, List<String>>>() {
      });
    var actualLink = properties.get(LINK.getValue()).getFirst();
    var actualTerm = properties.get(TERM.getValue()).getFirst();
    assertThat(actualLink).isEqualTo(LINK_PREFIX + expectedLinkSuffix);
    assertThat(actualTerm).isEqualTo(expectedTerm);
  }

  @Test
  void shouldGracefullyIgnoreControlFieldLessThan23Characters() {
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
