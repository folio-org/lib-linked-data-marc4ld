package org.folio.marc4ld.service.marc2ld.bib.mapper.custom;

import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FORM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getFirstOutgoingEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class ConceptFormMapperTest {

  @Mock
  private LabelService labelService;

  @Mock
  private MapperHelper mapperHelper;

  @Mock
  private FingerprintHashService hashService;

  @InjectMocks
  private ConceptFormMapper mapper;

  @ParameterizedTest
  @CsvSource({
    "a, false",
    "c, true",
    "d, true",
    "e, true",
    "f, true",
    "i, true",
    "j, true",
    "l, true",
    "m, true",
    "r, true",
    "s, true",
    "t, true",
    "v, true",
    "w, true",
    "y, true",
    "z, true",
    "5, true",
    "6, true",
    "7, false",
  })
  void isSupportedCode(char code, boolean expectedResult) {
    // when
    var result = mapper.isSupportedCode(code);

    // then
    assertEquals(expectedResult, result);
  }

  @Test
  void addSubResource() {
    // given
    var resource = new Resource();
    when(mapperHelper.getJsonNode(any())).thenReturn(null);
    when(hashService.hash(any())).thenReturn(null);

    // when
    mapper.addSubResource(resource, 'c');

    // then
    var outgoingEdges = resource.getOutgoingEdges();
    assertEquals(2, outgoingEdges.size());

    var conceptFormEdge = getEdge(resource, CONCEPT, FORM);
    assertTrue(conceptFormEdge.isPresent());
    assertEquals("http://bibfra.me/vocab/lite/subject", conceptFormEdge.get().getPredicate().getUri());
    var conceptForm = conceptFormEdge.get().getTarget();
    assertEquals(1, conceptFormEdge.get().getTarget().getOutgoingEdges().size());

    var formEdge = getEdge(conceptForm, FORM);
    assertTrue(formEdge.isPresent());
    assertEquals("http://bibfra.me/vocab/lite/focus", formEdge.get().getPredicate().getUri());
    var form = formEdge.get().getTarget();
    assertEquals(1, form.getOutgoingEdges().size());

    var lccnEdge = getEdge(form, ID_LCCN, IDENTIFIER);
    assertTrue(lccnEdge.isPresent());
    assertEquals("http://library.link/vocab/map", lccnEdge.get().getPredicate().getUri());
    assertTrue(lccnEdge.get().getTarget().getOutgoingEdges().isEmpty());

    var genreEdge = getFirstOutgoingEdge(resource, withPredicateUri("http://bibfra.me/vocab/lite/genre"));
    assertEquals(form, genreEdge.getTarget());
  }
}
