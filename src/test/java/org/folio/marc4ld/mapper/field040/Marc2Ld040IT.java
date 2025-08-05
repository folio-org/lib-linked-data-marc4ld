package org.folio.marc4ld.mapper.field040;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ANNOTATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.LANGUAGE_CATEGORY;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateResource;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld040IT extends Marc2LdTestBase {
  @Test
  void shouldMapField040() {
    // given
    var marc = loadResourceAsString("fields/040/marc_040.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    assertThat(result)
      .extracting(this::getAdminMetadata)
      .satisfies(r -> validateResource(r,
          List.of(ANNOTATION),
          Map.of(
            "http://bibfra.me/vocab/marc/catalogingAgency", List.of("DLC"),
            "http://bibfra.me/vocab/marc/transcribingAgency", List.of("CtY"),
            "http://bibfra.me/vocab/marc/modifyingAgency", List.of("MH"),
            "http://bibfra.me/vocab/marc/controlNumber", List.of("123456789"),
            "http://bibfra.me/vocab/lite/createdDate", List.of("018015")
          ),
          "123456789"))
      .extracting(this::getCatalogingLanguage)
      .satisfies(r -> validateResource(r,
          List.of(LANGUAGE_CATEGORY),
          Map.of(
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/vocabulary/languages/fre"),
            "http://bibfra.me/vocab/marc/code", List.of("fre")
          ),
          "fre"));
  }

  private Resource getCatalogingLanguage(Resource resource) {
    return getFirstOutgoingEdge(resource, withPredicateUri("http://bibfra.me/vocab/lite/catalogingLanguage")).getTarget();
  }

  private Resource getAdminMetadata(Resource resource) {
    return getFirstOutgoingEdge(resource, withPredicateUri("http://bibfra.me/vocab/marc/adminMetadata")).getTarget();
  }
}
