package org.folio.marc4ld.mapper.field300;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.EXTENT;
import static org.folio.ld.dictionary.PropertyDictionary.ACCOMPANYING_MATERIAL;
import static org.folio.ld.dictionary.PropertyDictionary.DIMENSIONS;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.MATERIALS_SPECIFIED;
import static org.folio.ld.dictionary.PropertyDictionary.PHYSICAL_DESCRIPTION;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.test.helper.ResourceEdgeHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class MarcToLd300IT extends Marc2LdTestBase {

  @Test
  void shouldMapField300() {
    // given
    var marc = loadResourceAsString("fields/300/marc_300.jsonl");
    var expectedExtentLabel = "extent_1/physical_description_1 extent_2 extent_3";

    //when
    var result = marcBibToResource(marc);

    //then
    assertThat(result)
      .satisfies(r -> validateResource(r, List.of(ResourceTypeDictionary.INSTANCE),
        Map.of(
          PropertyDictionary.EXTENT.getValue(), List.of("extent_1/physical_description_1"),
          DIMENSIONS.getValue(), List.of("dimensions"),
          PHYSICAL_DESCRIPTION.getValue(), List.of("physical_description_2"),
          ACCOMPANYING_MATERIAL.getValue(), List.of("accompanyingMaterial")
        ), ""))
      .extracting(this::getExtentEdge)
      .satisfies(e -> validateEdge(e, EXTENT, List.of(ResourceTypeDictionary.EXTENT),
        Map.of(
          LABEL.getValue(), List.of(expectedExtentLabel),
          MATERIALS_SPECIFIED.getValue(), List.of("materials spec")
        ), expectedExtentLabel))
      .extracting(ResourceEdgeHelper::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  private ResourceEdge getExtentEdge(Resource resource) {
    return getFirstOutgoingEdge(resource, withPredicateUri(EXTENT.getUri()));
  }

}
