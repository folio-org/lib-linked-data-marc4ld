package org.folio.marc4ld;

import static org.assertj.core.api.Assertions.assertThat;

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
}
