package org.folio.marc4ld.service.marc2ld;

import static java.lang.Character.MIN_VALUE;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.util.BibframeUtil.getFirstValue;
import static org.folio.marc4ld.util.BibframeUtil.hash;
import static org.folio.marc4ld.util.BibframeUtil.isNotEmptyResource;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;
import static org.folio.marc4ld.util.Constants.SUBFIELD_SRS_ID;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.model.Resource;
import org.folio.marc4ld.service.marc2ld.field.FieldMapper;
import org.marc4j.MarcJsonReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.impl.DataFieldImpl;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class Marc2BibframeMapperImpl implements Marc2BibframeMapper {
  private final Marc4BibframeRules rules;
  private final ObjectMapper objectMapper;
  private final FieldMapper fieldMapper;

  @Override
  public Resource fromMarcJson(String marc) {
    if (isEmpty(marc)) {
      log.warn("Given marc is empty [{}]", marc);
      return null;
    }
    var reader = new MarcJsonReader(new ByteArrayInputStream(marc.getBytes(StandardCharsets.UTF_8)));
    var instance = new Resource().addType(INSTANCE);
    while (reader.hasNext()) {
      var marcRecord = reader.next();
      marcRecord.getDataFields().forEach(dataField -> {
        handleField(dataField.getTag(), instance, dataField, marcRecord);
        if (FIELD_UUID.equals(dataField.getTag())) {
          instance.setInventoryId(readUuid(dataField.getSubfield(SUBFIELD_INVENTORY_ID)));
          instance.setSrsId(readUuid(dataField.getSubfield(SUBFIELD_SRS_ID)));
        }
      });
      marcRecord.getControlFields().forEach(controlField -> handleField(controlField.getTag(), instance,
        new DataFieldImpl(EMPTY, MIN_VALUE, MIN_VALUE), marcRecord));
    }
    instance.setLabel(selectInstanceLabel(instance));
    cleanEmptyEdges(instance);
    instance.setResourceHash(hash(instance, objectMapper));
    setEdgesId(instance);
    return instance;
  }

  private void handleField(String tag, Resource instance, DataField dataField, org.marc4j.marc.Record marcRecord) {
    ofNullable(rules.getFieldRules().get(tag)).ifPresent(frs ->
      frs.forEach(fr -> fieldMapper.handleField(instance, dataField, marcRecord.getControlFields(), fr))
    );
  }

  private UUID readUuid(Subfield subfield) {
    if (isNull(subfield) || isNull(subfield.getData())) {
      return null;
    }
    var value = subfield.getData().strip();
    try {
      return UUID.fromString(value);
    } catch (Exception e) {
      log.warn("Incorrect UUID value from Marc field 999, subfield [{}]: {}", subfield.getCode(), value);
      return null;
    }
  }

  private String selectInstanceLabel(Resource instance) {
    return getFirstValue(() -> instance.getOutgoingEdges().stream()
      .filter(e -> TITLE.getUri().equals(e.getPredicate().getUri()))
      .map(re -> re.getTarget().getLabel()).toList());
  }

  private void cleanEmptyEdges(Resource resource) {
    resource.setOutgoingEdges(resource.getOutgoingEdges().stream()
      .map(re -> {
        cleanEmptyEdges(re.getTarget());
        return re;
      })
      .filter(re -> isNotEmptyResource(re.getTarget()))
      .collect(Collectors.toCollection(LinkedHashSet::new))
    );
  }

  private void setEdgesId(Resource resource) {
    resource.getOutgoingEdges().forEach(edge -> {
      edge.getId().setSourceHash(edge.getSource().getResourceHash());
      edge.getId().setTargetHash(edge.getTarget().getResourceHash());
      edge.getId().setPredicateHash(edge.getPredicate().getHash());
      setEdgesId(edge.getTarget());
    });
  }

}
