package org.folio.marc4ld;

import static org.assertj.core.api.Assertions.assertThat;

import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;

public class Marc2LdTestBase {
  private final FingerprintHashService hashService;

  public Marc2LdTestBase(FingerprintHashService hashService) {
    this.hashService = hashService;
  }

  protected void validateAllIds(Resource resource) {
    validateId(resource);
    resource.getOutgoingEdges().stream()
      .map(ResourceEdge::getTarget)
      .forEach(this::validateAllIds);
  }

  private void validateId(Resource resource) {
    var expectedId = hashService.hash(resource);
    assertThat(resource.getId()).isEqualTo(expectedId);
  }
}
