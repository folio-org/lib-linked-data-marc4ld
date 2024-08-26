package org.folio.marc4ld;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.model.ResourceSource.MARC;

import java.util.Collection;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.marc2ld.authority.MarcAuthority2ldMapper;
import org.folio.marc4ld.service.marc2ld.bib.MarcBib2ldMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
public class Marc2LdTestBase {

  @Autowired
  private FingerprintHashService hashService;

  @Autowired
  private MarcBib2ldMapper marc2BibframeMapper;

  @Autowired
  private MarcAuthority2ldMapper marcAuthority2ldMapper;

  protected Resource marcBibToResource(String marc) {
    var optionalResource = marc2BibframeMapper.fromMarcJson(marc);
    assertThat(optionalResource).isNotEmpty();
    var resource = optionalResource.get();
    validateSource(resource);
    validateAllIds(resource);
    return resource;
  }

  protected Collection<Resource> marcAuthorityToResources(String marc) {
    var resources = marcAuthority2ldMapper.fromMarcJson(marc);
    resources.forEach(this::validateAllIds);
    return resources;
  }

  protected void validateId(Resource resource) {
    var expectedId = hashService.hash(resource);
    assertThat(resource.getId()).isEqualTo(expectedId);
  }

  protected void validateAllIds(Resource resource) {
    validateId(resource);
    resource.getOutgoingEdges().stream()
      .map(ResourceEdge::getTarget)
      .forEach(this::validateAllIds);
  }

  private void validateSource(Resource resource) {
    assertThat(resource.getFolioMetadata().getSource()).isEqualTo(MARC);
  }
}
