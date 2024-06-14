package org.folio.marc4ld.mapper.field337;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.PredicateDictionary.MEDIA;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.service.marc2ld.bib.MarcBib2ldMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MarcToBibframe337IT extends Marc2LdTestBase {

  @Autowired
  private MarcBib2ldMapper marc2BibframeMapper;

  @Test
  void shouldMapField337() {
    // given
    var marc = loadResourceAsString("fields/337/marc_337_rdamedia.jsonl");

    //when
    var result = marc2BibframeMapper.fromMarcJson(marc);

    //then
    assertThat(result)
      .isNotNull()
      .satisfies(this::validateAllIds)
      .extracting(this::getMediaEdge)
      .satisfies(e -> validateEdge(e, MEDIA, List.of(CATEGORY),
        Map.of(
          "http://bibfra.me/vocab/marc/code", List.of("MEDIA code"),
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/mediaTypes/MEDIA code"),
          "http://bibfra.me/vocab/marc/term", List.of("MEDIA term"),
          "http://bibfra.me/vocab/marc/source", List.of("MEDIA source")
        ),
        "MEDIA term"))
      .extracting(this::getFirstOutgoingEdge)
      .satisfies(e -> validateEdge(e, IS_DEFINED_BY, List.of(CATEGORY_SET),
        Map.of(
          "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/genreFormSchemes/rdamedia"),
          "http://bibfra.me/vocab/lite/label", List.of("rdamedia")
        ),
        "rdamedia"))
      .extracting(this::getOutgoingEdges)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .isEmpty();
  }

  private ResourceEdge getMediaEdge(Resource result) {
    return getFirstOutgoingEdge(result);
  }
}
