package org.folio.marc2ld.mapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.PredicateDictionary.valueOf;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc2ld.util.BibframeUtil.getFirstValue;
import static org.folio.marc2ld.util.BibframeUtil.hash;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.model.Resource;
import org.folio.marc2ld.model.ResourceEdge;
import org.marc4j.MarcJsonReader;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Marc2BibframeMapperImpl implements Marc2BibframeMapper {

  private static final String NOT = "!";
  private final Marc2BibframeRules rules;
  private final ObjectMapper objectMapper;

  @Override
  public Resource map(String marc) {
    if (isEmpty(marc)) {
      return null;
    }
    var reader = new MarcJsonReader(new ByteArrayInputStream(marc.getBytes(StandardCharsets.UTF_8)));
    var instance = new Resource().addType(INSTANCE);
    while (reader.hasNext()) {
      var marcRecord = reader.next();
      for (var dataField : marcRecord.getDataFields()) {
        if (isNotEmpty(dataField.getTag())) {
          var fieldRules = rules.getFieldRules().get(dataField.getTag());
          if (nonNull(fieldRules)) {
            fieldRules.forEach(fieldRule -> addFieldResource(instance, dataField, fieldRule));
          }
        }
      }
    }
    instance.setLabel(selectLabel(instance));
    instance.setResourceHash(hash(instance, objectMapper));
    setEdgesId(instance);
    return instance;
  }

  private void setEdgesId(Resource resource) {
    resource.getOutgoingEdges().forEach(edge -> {
      edge.getId().setSourceHash(edge.getSource().getResourceHash());
      edge.getId().setTargetHash(edge.getTarget().getResourceHash());
      edge.getId().setPredicateHash(edge.getPredicate().getHash());
      setEdgesId(edge.getTarget());
    });
  }

  private String selectLabel(Resource instance) {
    return getFirstValue(() -> instance.getOutgoingEdges().stream()
      .filter(e -> TITLE.getUri().equals(e.getPredicate().getUri()))
      .map(re -> re.getTarget().getLabel()).toList());
  }

  private void addFieldResource(Resource instance, DataField dataField, Marc2BibframeRules.FieldRule fieldRule) {
    if (checkConditions(fieldRule, dataField)) {
      var resource = new Resource();
      fieldRule.getTypes().stream().map(ResourceTypeDictionary::valueOf).forEach(resource::addType);
      var properties = new HashMap<String, List<String>>();
      fieldRule.getSubfields().forEach((key, value) -> {
        if (nonNull(dataField.getSubfield(key))) {
          properties.computeIfAbsent(PropertyDictionary.valueOf(value).getValue(), k -> new ArrayList<>())
            .add(dataField.getSubfield(key).getData());
        }
      });
      if (nonNull(fieldRule.getInd1())) {
        properties.computeIfAbsent(PropertyDictionary.valueOf(fieldRule.getInd1()).getValue(), k -> new ArrayList<>())
          .add(String.valueOf(dataField.getIndicator1()));
      }
      if (nonNull(fieldRule.getInd2())) {
        properties.computeIfAbsent(PropertyDictionary.valueOf(fieldRule.getInd2()).getValue(), k -> new ArrayList<>())
          .add(String.valueOf(dataField.getIndicator2()));
      }
      resource.setDoc(getJsonNode(properties));
      var labelField = dataField.getSubfield(fieldRule.getLabelField());
      if (nonNull(labelField)) {
        resource.setLabel(labelField.getData());
      }
      resource.setResourceHash(hash(resource, objectMapper));
      instance.getOutgoingEdges().add(new ResourceEdge(instance, resource,
        valueOf(fieldRule.getPredicate())));
    }
  }

  private JsonNode getJsonNode(Map<String, ?> map) {
    return objectMapper.convertValue(map, JsonNode.class);
  }

  private boolean checkConditions(Marc2BibframeRules.FieldRule fieldRule, DataField dataField) {
    var condition = fieldRule.getCondition();
    if (isNull(condition)) {
      return true;
    }
    boolean ind1Condition = checkCondition(String.valueOf(dataField.getIndicator1()), condition.getInd1());
    boolean ind2Condition = checkCondition(String.valueOf(dataField.getIndicator2()), condition.getInd2());
    boolean fieldConditions = condition.getFields().entrySet().stream()
      .allMatch(fieldCondition -> ofNullable(dataField.getSubfield(fieldCondition.getKey()))
        .map(sf -> checkCondition(sf.getData(), fieldCondition.getValue()))
        .orElse(false));
    return ind1Condition && ind2Condition && fieldConditions;
  }

  private boolean checkCondition(String value, String condition) {
    if (isEmpty(condition)) {
      return true;
    }
    if (condition.contains(NOT)) {
      condition = condition.replace(NOT, "");
      return !Objects.equals(value, condition);
    }
    return Objects.equals(value, condition);
  }
}
