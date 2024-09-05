package org.folio.marc4ld.mapper.field082;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ASSIGNING_SOURCE;
import static org.folio.ld.dictionary.PredicateDictionary.CLASSIFICATION;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.EDITION;
import static org.folio.ld.dictionary.PropertyDictionary.EDITION_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.ITEM_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.SOURCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.marc4ld.mapper.test.MonographTestUtil.createResource;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.util.Constants.Classification.ABRIDGED;
import static org.folio.marc4ld.util.Constants.Classification.DDC;
import static org.folio.marc4ld.util.Constants.Classification.DLC;
import static org.folio.marc4ld.util.Constants.Classification.FULL;
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
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class Marc2Bibframe082IT extends Marc2LdTestBase {

  private static Stream<Arguments> provideArguments() {
    return Stream.of(
      Arguments.of(
        "fields/082/full_edition_with_lc_source.jsonl",
        createDdcClassification(FULL, createAssigningSource(DLC))
      ),
      Arguments.of(
        "fields/082/abridged_edition_with_other_source.jsonl",
        createDdcClassification(ABRIDGED, createAssigningSource(null))
      ),
      Arguments.of(
        "fields/082/other_edition_without_source_info.jsonl",
        createDdcClassification(null, null)
      )
    );
  }

  @ParameterizedTest
  @MethodSource("provideArguments")
  void shouldMapField082Correctly(String marcFile, Resource expectedResource) {
    //given
    var marc = loadResourceAsString(marcFile);

    //when
    var result = marcBibToResource(marc);

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

  static Resource createDdcClassification(String edition, Resource assigningSource) {
    var properties = new LinkedHashMap<PropertyDictionary, List<String>>();
    properties.put(CODE, List.of("code"));
    properties.put(SOURCE, List.of(DDC));
    properties.put(ITEM_NUMBER, List.of("item number"));
    properties.put(EDITION_NUMBER, List.of("edition number"));
    if (edition != null) {
      properties.put(EDITION, List.of(edition));
    }
    var outgoingEdges = new LinkedHashMap<PredicateDictionary, List<Resource>>();
    if (assigningSource != null) {
      outgoingEdges.put(ASSIGNING_SOURCE, List.of(assigningSource));
    }
    return createResource(properties, Set.of(ResourceTypeDictionary.CLASSIFICATION), outgoingEdges).setLabel("code");
  }

  public static Resource createAssigningSource(String link) {
    return link == null
      ? createResource(
      Map.of(
        LABEL, List.of("assigning agency"),
        NAME, List.of("assigning agency")
      ),
      Set.of(ORGANIZATION),
      emptyMap()
    ).setLabel("assigning agency")
      : createResource(
      Map.of(
        LABEL, List.of("United States, Library of Congress"),
        NAME, List.of("United States, Library of Congress"),
        LINK, List.of(link)
      ),
      Set.of(ORGANIZATION),
      emptyMap()
    ).setLabel("United States, Library of Congress");
  }
}
