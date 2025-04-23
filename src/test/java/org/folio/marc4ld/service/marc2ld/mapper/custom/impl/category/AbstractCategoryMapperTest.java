package org.folio.marc4ld.service.marc2ld.mapper.custom.impl.category;

import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.PredicateDictionary.NULL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.Set;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.mapper.MapperHelper;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;

@UnitTest
class AbstractCategoryMapperTest {

  LabelService labelService = mock(LabelService.class);
  MapperHelper mapperHelper = mock(MapperHelper.class);
  FingerprintHashService hashService = mock(FingerprintHashService.class);
  AbstractCategoryMapper mapper = new TestCategoryMapper(labelService, mapperHelper, hashService);

  @Test
  void addSubResource() {
    // given
    var work = new Resource();

    // when
    mapper.addSubResource(work, 'c');

    // then
    var workEdges = work.getOutgoingEdges();
    assertEquals(1, workEdges.size());
    assertEquals(NULL, workEdges.iterator().next().getPredicate());

    var subResource = workEdges.iterator().next().getTarget();
    assertNotNull(subResource.getId());
    assertEquals(Set.of(CATEGORY), subResource.getTypes());
    assertEquals(1, subResource.getOutgoingEdges().size());
    assertEquals(IS_DEFINED_BY, subResource.getOutgoingEdges().iterator().next().getPredicate());
    assertEquals(Set.of(CATEGORY_SET), subResource.getOutgoingEdges().iterator().next().getTarget().getTypes());
    assertEquals(0, subResource.getOutgoingEdges().iterator().next().getTarget().getOutgoingEdges().size());
  }

  static class TestCategoryMapper extends AbstractCategoryMapper {

    TestCategoryMapper(LabelService labelService, MapperHelper mapperHelper, FingerprintHashService hashService) {
      super(labelService, mapperHelper, hashService, 0, 2, "categorySetLabel", "categorySetLink");
    }

    @Override
    protected boolean isSupportedCode(char code) {
      return 'a' == code;
    }

    @Override
    protected PredicateDictionary getPredicate() {
      return NULL;
    }

    @Override
    protected String getLinkSuffix(char code) {
      return "linkSuffix";
    }

    @Override
    protected String getTerm(char code) {
      return "term";
    }

    @Override
    protected String getCode(char code) {
      return "code";
    }
  }
}
