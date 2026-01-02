package org.folio.marc4ld.service.marc2ld.authority.identifier;

import static java.util.Optional.ofNullable;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Component;

@Component
public class Marc001IdentifierMapper extends AbstractIdentifierMapper {
  public Marc001IdentifierMapper(LabelService labelService,
                                 IdentifierLinkService identifierLinkService,
                                 MapperHelper mapperHelper,
                                 FingerprintHashService hashService) {
    super(labelService, identifierLinkService, mapperHelper, hashService);
  }

  @Override
  public boolean isApplicable(Record marcRecord) {
    return getControlNumber(marcRecord).isPresent();
  }

  @Override
  public void map(Record marcRecord, Resource authority) {
    getControlNumber(marcRecord)
      .ifPresent(id -> addIdentifierEdge(authority, id));
  }

  private Optional<String> getControlNumber(Record marc) {
    return ofNullable(marc.getControlNumber())
      .map(StringUtils::deleteWhitespace);
  }
}
