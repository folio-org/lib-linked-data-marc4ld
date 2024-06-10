package org.folio.marc4ld.mapper.field050;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ASSIGNING_SOURCE;
import static org.folio.ld.dictionary.PredicateDictionary.CLASSIFICATION;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.ITEM_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.SOURCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.STATUS;
import static org.folio.marc4ld.mapper.field082.Marc2Bibframe082IT.createAssigningSource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.util.Constants.Classification.DLC;
import static org.folio.marc4ld.util.Constants.Classification.LC;
import static org.folio.marc4ld.util.Constants.Classification.NUBA;
import static org.folio.marc4ld.util.Constants.Classification.UBA;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.mapper.test.MonographTestUtil;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.marc2ld.bib.MarcBib2LdMapperImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Marc2Bibframe050IT {

  @Autowired
  private MarcBib2LdMapperImpl marc2BibframeMapper;

  private static Stream<Arguments> provideArguments() {
    return Stream.of(
      Arguments.of(
        "fields/050/used_by_assigner_with_lc_source.jsonl",
        createLcClassification(createAssigningSource(DLC), createStatus(UBA))
      ),
      Arguments.of(
        "fields/050/not_used_by_assigner_with_other_source.jsonl",
        createLcClassification(null, createStatus(NUBA))
      ),
      Arguments.of(
        "fields/050/no_usage_information_with_lc_source.jsonl",
        createLcClassification(createAssigningSource(DLC), null)
      )
    );
  }

  @ParameterizedTest
  @MethodSource("provideArguments")
  void shouldMapField082Correctly(String marcFile, Resource expectedResource) {
    //given
    var marc = loadResourceAsString(marcFile);

    //when
    var result = marc2BibframeMapper.fromMarcJson(marc);

    //then
    var work = result.getOutgoingEdges().iterator().next().getTarget();
    assertThat(work.getOutgoingEdges()).hasSize(1);
    var resourceEdge = work.getOutgoingEdges().iterator().next();
    assertEquals(CLASSIFICATION, resourceEdge.getPredicate());
    assertThat(resourceEdge.getTarget())
      .usingRecursiveComparison()
      .ignoringFields("id", "outgoingEdges.id", "outgoingEdges.source.id", "outgoingEdges.target.id")
      .isEqualTo(expectedResource);
  }

  static Resource createLcClassification(Resource assigningSource, Resource status) {
    var properties = new LinkedHashMap<PropertyDictionary, List<String>>();
    properties.put(CODE, List.of("code"));
    properties.put(SOURCE, List.of(LC));
    properties.put(ITEM_NUMBER, List.of("item number"));
    var outgoingEdges = new LinkedHashMap<PredicateDictionary, List<Resource>>();
    if (status != null) {
      outgoingEdges.put(PredicateDictionary.STATUS, List.of(status));
    }
    if (assigningSource != null) {
      outgoingEdges.put(ASSIGNING_SOURCE, List.of(assigningSource));
    }
    return MonographTestUtil.createResource(properties, Set.of(ResourceTypeDictionary.CLASSIFICATION), outgoingEdges)
      .setLabel("code");
  }

  static Resource createStatus(String link) {
    return UBA.equals(link)
      ? MonographTestUtil.createResource(
      Map.of(
        LABEL, List.of("used by assigner"),
        LINK, List.of(link)
      ),
      Set.of(STATUS),
      emptyMap()
    ).setLabel("used by assigner")
      : MonographTestUtil.createResource(
      Map.of(
        LABEL, List.of("not used by assigner"),
        LINK, List.of(link)
      ),
      Set.of(STATUS),
      emptyMap()
    ).setLabel("not used by assigner");
  }
}
