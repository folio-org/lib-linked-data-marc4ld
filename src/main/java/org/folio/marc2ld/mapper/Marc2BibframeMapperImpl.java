package org.folio.marc2ld.mapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc2ld.util.BibframeUtil.getFirstValue;
import static org.folio.marc2ld.util.BibframeUtil.hash;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.mapper.field.FieldMapper;
import org.folio.marc2ld.model.Resource;
import org.marc4j.MarcJsonReader;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class Marc2BibframeMapperImpl implements Marc2BibframeMapper {
  private static final String FIELD_UUID = "999";
  private static final char SUBFIELD_INVENTORY_ID = 'i';
  private static final char SUBFIELD_SRS_ID = 's';
  private final Marc2BibframeRules rules;
  private final ObjectMapper objectMapper;
  private final FieldMapper fieldMapper;

  @Override
  public Resource map(String marc) {
    if (isEmpty(marc)) {
      return null;
    }
    var reader = new MarcJsonReader(new ByteArrayInputStream(marc.getBytes(StandardCharsets.UTF_8)));
    var instance = new Resource().addType(INSTANCE);
    while (reader.hasNext()) {
      var marcRecord = reader.next();
      marcRecord.getDataFields().forEach(dataField -> {
        var fieldRules = rules.getFieldRules().get(dataField.getTag());
        if (nonNull(fieldRules)) {
          fieldRules.forEach(fieldRule -> fieldMapper.handleField(instance, dataField, marcRecord.getControlFields(),
            fieldRule));
        } else if (FIELD_UUID.equals(dataField.getTag())) {
          instance.setInventoryId(readUuid(dataField.getSubfield(SUBFIELD_INVENTORY_ID)));
          instance.setSrsId(readUuid(dataField.getSubfield(SUBFIELD_SRS_ID)));
        }
      });
    }
    instance.setLabel(selectInstanceLabel(instance));
    cleanEmptyEdges(instance);
    instance.setResourceHash(hash(instance, objectMapper));
    setEdgesId(instance);
    return instance;
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
      .filter(re -> nonNull(re.getTarget().getDoc()) && !re.getTarget().getDoc().isEmpty()
        || !re.getTarget().getOutgoingEdges().isEmpty())
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
