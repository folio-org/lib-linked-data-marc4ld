package org.folio.marc4ld.service.marc2ld.authority.identifier;

import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.TAG_010;
import static org.folio.marc4ld.util.MarcUtil.getSubfieldValueWithoutSpaces;

import java.util.Optional;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Component;

@Component
public class Marc010IdentifierMapper extends AbstractIdentifierMapper {

  public Marc010IdentifierMapper(LabelService labelService,
                                 IdentifierLinkProvider identifierLinkProvider,
                                 MapperHelper mapperHelper,
                                 FingerprintHashService hashService,
                                 IdentifierPrefixService identifierPrefixService) {
    super(labelService, identifierLinkProvider, mapperHelper, hashService, identifierPrefixService);
  }

  @Override
  protected Optional<String> getIdentifier(Record marc) {
    return marc.getDataFields().stream()
      .filter(df -> df.getTag().equals(TAG_010))
      .flatMap(df -> getSubfieldValueWithoutSpaces(df, A).stream())
      .findFirst();
  }
}
