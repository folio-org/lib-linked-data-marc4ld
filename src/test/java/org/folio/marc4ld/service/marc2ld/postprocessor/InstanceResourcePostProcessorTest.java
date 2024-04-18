package org.folio.marc4ld.service.marc2ld.postprocessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.EDITOR;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.marc4ld.mapper.test.TestUtil.OBJECT_MAPPER;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.stream.Stream;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.marc2ld.postprocessor.impl.InstanceResourcePostProcessorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InstanceResourcePostProcessorTest {

  @Mock
  private FingerprintHashService fingerprintHashService;
  @InjectMocks
  private InstanceResourcePostProcessorImpl instanceResourcePostProcessor;

  @Test
  void shouldCleanEmptyEdges() {
    // given
    var emptyResource = createResource(1L, null);
    var nonEmptyResource1 = createResource(2L, OBJECT_MAPPER.createObjectNode().put("k", "v"));
    var nonEmptyResource2 = createResource(3L, null, nonEmptyResource1);
    var testResource = createResource(4L, null, nonEmptyResource1, nonEmptyResource2, emptyResource);

    // when
    instanceResourcePostProcessor.apply(testResource);

    // then
    assertThat(testResource.getOutgoingEdges()).hasSize(2);
    assertThat(testResource.getOutgoingEdges().stream().map(ResourceEdge::getTarget))
      .containsExactlyInAnyOrder(nonEmptyResource1, nonEmptyResource2);
  }

  @Test
  void shouldAssignIdRecursively() {
    // given
    var resource1 = createResource(1L, OBJECT_MAPPER.createObjectNode().put("k", "v"));
    var resource2 = createResource(2L, null, resource1);
    var resource3 = createResource(3L, null, resource1);
    var testResource = createResource(4L, null, resource2, resource3);
    when(fingerprintHashService.hash(resource1)).thenReturn(100L);
    when(fingerprintHashService.hash(resource2)).thenReturn(200L);
    when(fingerprintHashService.hash(resource3)).thenReturn(300L);
    when(fingerprintHashService.hash(testResource)).thenReturn(400L);

    // when
    instanceResourcePostProcessor.apply(testResource);

    // then
    assertThat(resource1.getId()).isEqualTo(100L);
    assertThat(resource2.getId()).isEqualTo(200L);
    assertThat(resource3.getId()).isEqualTo(300L);
    assertThat(testResource.getId()).isEqualTo(400L);
  }

  @Test
  void shouldSetInstanceLabel() {
    // given
    var expectedLabel = "instance title";
    var instance = createResource(2L, null);
    var title = createResource(3L, null).setLabel(expectedLabel);
    createConnection(instance, title, TITLE);

    // when
    instanceResourcePostProcessor.apply(instance);

    // then
    assertThat(instance.getLabel()).isEqualTo(expectedLabel);
  }

  @Test
  void shouldSetWorkLabel() {
    // given
    var expectedLabel = "work title";
    var title = createResource(3L, null).setLabel(expectedLabel);
    var instance = createResource(2L, null);
    var work = createResource(3L, null);
    createConnection(instance, title, TITLE);
    createConnection(instance, work, INSTANTIATES);

    // when
    instanceResourcePostProcessor.apply(instance);

    // then
    assertThat(work.getLabel()).isEqualTo(expectedLabel);
  }

  private Resource createResource(Long id, JsonNode doc, Resource... edges) {
    var resource = new Resource().setId(id).setDoc(doc);
    Stream.of(edges)
      .forEach(e -> createConnection(resource, e, EDITOR));
    return resource;
  }

  private void createConnection(Resource from, Resource to, PredicateDictionary predicate) {
    from.getOutgoingEdges().add(new ResourceEdge(from, to, predicate));
  }
}
