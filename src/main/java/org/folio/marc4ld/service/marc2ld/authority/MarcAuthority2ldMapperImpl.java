package org.folio.marc4ld.service.marc2ld.authority;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.folio.ld.dictionary.model.ResourceSource.MARC;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.S;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;
import static org.folio.marc4ld.util.Constants.TAG_010;
import static org.folio.marc4ld.util.MarcUtil.getSubfieldValueStripped;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.ld.dictionary.model.FolioMetadata;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.condition.ConditionChecker;
import org.folio.marc4ld.service.marc2ld.Marc2ldRules;
import org.folio.marc4ld.service.marc2ld.authority.control.AuthorityIdentifierProcessor;
import org.folio.marc4ld.service.marc2ld.field.ResourceProcessor;
import org.folio.marc4ld.service.marc2ld.reader.MarcReaderProcessor;
import org.folio.marc4ld.service.marc2ld.relation.EmptyEdgesCleaner;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class MarcAuthority2ldMapperImpl implements MarcAuthority2ldMapper {

  private final Marc2ldRules rules;
  private final ConditionChecker conditionChecker;
  private final ResourceProcessor fieldController;
  private final FingerprintHashService hashService;
  private final MarcReaderProcessor marcReaderProcessor;
  private final EmptyEdgesCleaner emptyEdgesCleaner;
  private final AuthorityIdentifierProcessor authorityIdentifierProcessor;

  @Override
  public Collection<Resource> fromMarcJson(String marc) {
    if (isEmpty(marc)) {
      log.warn("Given marc is empty [{}]", marc);
      return Collections.emptyList();
    }
    return marcReaderProcessor.readMarc(marc)
      .filter(this::hasLccn)
      .flatMap(this::createResources)
      .map(emptyEdgesCleaner)
      .toList();
  }

  private boolean hasLccn(Record marcRecord) {
    return marcRecord.getDataFields().stream().anyMatch(this::isLccn);
  }

  private boolean isLccn(DataField dataField) {
    return dataField.getTag().equals(TAG_010)
      && dataField.getSubfields().stream().anyMatch(subfield -> subfield.getCode() == A);
  }

  private Stream<Resource> createResources(org.marc4j.marc.Record marcRecord) {
    return marcRecord.getDataFields()
      .stream()
      .flatMap(dataField -> createResources(dataField, marcRecord))
      .map(r -> fillResource(r, marcRecord));
  }

  private Stream<Resource> createResources(DataField dataField, org.marc4j.marc.Record marcRecord) {
    return rules.findAuthorityFieldRules(dataField.getTag())
      .stream()
      .filter(rule -> conditionChecker
        .isMarc2LdConditionSatisfied(rule.getOriginal(), dataField, marcRecord.getControlFields()))
      .map(applier -> fieldController.create(dataField, marcRecord.getControlFields(), applier))
      .flatMap(Collection::stream);
  }

  private Resource fillResource(Resource resource, org.marc4j.marc.Record marcRecord) {
    marcRecord.getDataFields()
      .forEach(controlField -> authorityIdentifierProcessor.setIdentifier(resource, controlField));
    resource.setId(hashService.hash(resource));
    folioMetadataFrom(marcRecord).ifPresent(resource::setFolioMetadata);
    return resource;
  }

  private Optional<FolioMetadata> folioMetadataFrom(org.marc4j.marc.Record marcRecord) {
    return marcRecord.getDataFields().stream()
      .filter(dataField -> dataField.getTag().equals(FIELD_UUID))
      .findFirst().map(
        metadata -> new FolioMetadata()
          .setSource(MARC)
          .setInventoryId(getSubfieldValueStripped(metadata, SUBFIELD_INVENTORY_ID).orElse(null))
          .setSrsId(getSubfieldValueStripped(metadata, S).orElse(null))
      );
  }
}
