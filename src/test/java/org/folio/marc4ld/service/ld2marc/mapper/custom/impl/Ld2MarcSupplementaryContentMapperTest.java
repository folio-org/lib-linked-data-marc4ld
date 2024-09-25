package org.folio.marc4ld.service.ld2marc.mapper.custom.impl;

import static java.util.Collections.emptyMap;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.SUPPLEMENTARY_CONTENT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createCategory;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createCategorySet;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.mapper.custom.Ld2MarcCustomMapper;
import org.folio.marc4ld.service.ld2marc.resource.field.ControlFieldsBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Ld2MarcSupplementaryContentMapperTest {

  Ld2MarcSupplementaryContentMapper mapper = new Ld2MarcSupplementaryContentMapper();

  @ParameterizedTest
  @MethodSource("resourceProvider")
  void shouldNotMap(Resource resource) {
    //given
    var controlFieldsBuilder = mock(ControlFieldsBuilder.class);
    var context = new Ld2MarcCustomMapper.Context(controlFieldsBuilder, List.of());

    //when
    mapper.map(resource, context);

    //then
    verifyNoInteractions(controlFieldsBuilder);
  }

  @Test
  void shouldMap() {
    //given
    var resource = createResourceWithSupplementaryContents();
    var controlFieldsBuilder = mock(ControlFieldsBuilder.class);
    var context = new Ld2MarcCustomMapper.Context(controlFieldsBuilder, List.of());

    //when
    mapper.map(resource, context);

    //then
    verify(controlFieldsBuilder).addFieldValue("008", "b", 24, 28);
    verify(controlFieldsBuilder).addFieldValue("008", "1", 31, 32);
  }

  private static Stream<Arguments> resourceProvider() {
    return Stream.of(
      Arguments.of(new Resource()),
      Arguments.of(createResourceWithoutSupplementaryContents())
    );
  }

  private static Resource createResourceWithoutSupplementaryContents() {
    var work = createResource(
      emptyMap(),
      Set.of(WORK),
      emptyMap()
    );

    return createResource(
      emptyMap(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }

  private Resource createResourceWithSupplementaryContents() {
    var categorySet = createCategorySet("http://id.loc.gov/vocabulary/msupplcont", "Supplementary Content");
    var work = createResource(
      emptyMap(),
      Set.of(WORK),
      Map.of(SUPPLEMENTARY_CONTENT, List.of(
        createCategory("bibliography", "http://id.loc.gov/vocabulary/msupplcont/bibliography", "bibliography",
          categorySet),
        createCategory("music", "http://id.loc.gov/vocabulary/msupplcont/music", "music", categorySet),
        createCategory("index", "http://id.loc.gov/vocabulary/msupplcont/index", "index", categorySet)
      ))
    );

    return createResource(
      emptyMap(),
      Set.of(INSTANCE),
      Map.of(INSTANTIATES, List.of(work))
    );
  }
}
