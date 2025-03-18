package org.folio.marc4ld.mapper.field300;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.folio.ld.dictionary.PropertyDictionary.ACCOMPANYING_MATERIAL;
import static org.folio.ld.dictionary.PropertyDictionary.DIMENSIONS;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.MATERIALS_SPECIFIED;
import static org.folio.ld.dictionary.PropertyDictionary.PHYSICAL_DESCRIPTION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.EXTENT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
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
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Ld2Marc300IT {

  @Autowired
  private Ld2MarcMapper ld2MarcMapper;

  @Test
  void shouldMapField300() {
    // given
    var resource = createResourceWithExtentEdges();
    var expectedMarc = loadResourceAsString("fields/300/ld2marc_300.jsonl");

    //when
    var actualMarc = ld2MarcMapper.toMarcJson(resource);

    // then
    assertThat(actualMarc).isEqualTo(expectedMarc);
  }

  private Resource createResourceWithExtentEdges() {
    var extent1 = MonographTestUtil.createResource(
      Map.of(
        LABEL, List.of("extent1 label1", "extent1 label2"),
        MATERIALS_SPECIFIED, List.of("extent1 materials_spec1", "extent1 materials_spec2")
      ),
      Set.of(EXTENT),
      Collections.emptyMap()
    ).setLabel("extent unit_type unit_size");
    var extent2 = MonographTestUtil.createResource(
      Map.of(
        LABEL, List.of("extent2 label1", "extent2 label2"),
        MATERIALS_SPECIFIED, List.of("extent2 materials_spec1", "extent2 materials_spec2")
      ),
      Set.of(EXTENT),
      Collections.emptyMap()
    ).setLabel("extent2 unit_type2 unit_size2");
    return MonographTestUtil.createResource(
      Map.of(
        PropertyDictionary.EXTENT, List.of("extent"),
        DIMENSIONS, List.of("dimension1", "dimension2"),
        PHYSICAL_DESCRIPTION, List.of("physical_description1", "physical_description2"),
        ACCOMPANYING_MATERIAL, List.of("accompanying_material1", "accompanying_material2")
      ),
      Set.of(INSTANCE),
      Map.of(PredicateDictionary.EXTENT, List.of(extent1, extent2))
    );
  }
}
