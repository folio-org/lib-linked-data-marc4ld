package org.folio.marc4ld;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Predicate;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
public class Marc2LdTestBase {

  @Autowired
  private FingerprintHashService hashService;

  protected void validateAllIds(Resource resource) {
    validateId(resource);
    resource.getOutgoingEdges().stream()
      .map(ResourceEdge::getTarget)
      .forEach(this::validateAllIds);
  }

  protected void validateId(Resource resource) {
    var expectedId = hashService.hash(resource);
    assertThat(resource.getId()).isEqualTo(expectedId);
  }

  protected List<ResourceEdge> getOutgoingEdges(ResourceEdge resourceEdge) {
    return resourceEdge.getTarget()
      .getOutgoingEdges()
      .stream()
      .toList();
  }

  protected ResourceEdge getFirstOutgoingEdge(ResourceEdge resourceEdge, Predicate<ResourceEdge> predicate) {
    return getOutgoingEdges(resourceEdge)
      .stream()
      .filter(predicate)
      .findFirst()
      .orElseThrow();
  }

  protected ResourceEdge getFirstOutgoingEdge(Resource resource, Predicate<ResourceEdge> predicate) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(predicate)
      .findFirst()
      .orElseThrow();
  }

  protected ResourceEdge getWorkEdge(Resource instanceEdge) {
    return getFirstOutgoingEdge(instanceEdge, withPredicateUri("http://bibfra.me/vocab/lite/instantiates"));
  }

  protected Predicate<ResourceEdge> withPredicateUri(String uri) {
    return re -> re.getPredicate().getUri().equals(uri);
  }
}
