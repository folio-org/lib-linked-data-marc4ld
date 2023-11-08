package org.folio.marc2ld.mapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
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
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.model.Resource;
import org.folio.marc2ld.model.ResourceEdge;
import org.marc4j.MarcJsonReader;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class Marc2BibframeMapperImpl implements Marc2BibframeMapper {
  private static final String NOT = "!";
  private final Marc2BibframeRules rules;
  private final ObjectMapper objectMapper;

  private static void mapProperty(Map<String, List<String>> properties, String rule, String value, boolean concat) {
    if (nonNull(rule) && nonNull(value)) {
      value = value.strip();
      if (isNotEmpty(value)) {
        var key = PropertyDictionary.valueOf(rule).getValue();
        var keyProperties = properties.computeIfAbsent(key, k -> new ArrayList<>());
        if (concat && !keyProperties.isEmpty()) {
          var concatenated = keyProperties.get(0).concat(StringUtils.SPACE).concat(value);
          keyProperties.set(0, concatenated);
        } else {
          keyProperties.add(value);
        }
      }
    }
  }

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
        if (isNotEmptyDataField(dataField)) {
          var fieldRules = rules.getFieldRules().get(dataField.getTag());
          if (nonNull(fieldRules)) {
            fieldRules.forEach(fieldRule -> addFieldResource(instance, dataField, fieldRule));
          }
        }
      }
    }
    instance.setLabel(selectInstanceLabel(instance));
    instance.setResourceHash(hash(instance, objectMapper));
    setEdgesId(instance);
    return instance;
  }

  private boolean isNotEmptyDataField(DataField dataField) {
    return isNotEmpty(dataField.getTag()) && containsValue(dataField);
  }

  private boolean containsValue(DataField dataField) {
    return !CollectionUtils.isEmpty(dataField.getSubfields())
      || isNotEmptyIndicator(dataField.getIndicator1())
      || isNotEmptyIndicator(dataField.getIndicator2());
  }

  private void setEdgesId(Resource resource) {
    resource.getOutgoingEdges().forEach(edge -> {
      edge.getId().setSourceHash(edge.getSource().getResourceHash());
      edge.getId().setTargetHash(edge.getTarget().getResourceHash());
      edge.getId().setPredicateHash(edge.getPredicate().getHash());
      setEdgesId(edge.getTarget());
    });
  }

  private String selectInstanceLabel(Resource instance) {
    return getFirstValue(() -> instance.getOutgoingEdges().stream()
      .filter(e -> TITLE.getUri().equals(e.getPredicate().getUri()))
      .map(re -> re.getTarget().getLabel()).toList());
  }

  private void addFieldResource(Resource instance, DataField dataField, Marc2BibframeRules.FieldRule fieldRule) {
    if (checkConditions(fieldRule, dataField)) {
      if (fieldRule.getTypes().contains(INSTANCE.name())) {
        enrichInstance(instance, dataField, fieldRule);
      } else {
        var parentResource = selectParent(instance, fieldRule.getParent());
        if (isNull(parentResource)) {
          parentResource = new Resource();
          parentResource.addType(ResourceTypeDictionary.valueOf(fieldRule.getParent()));
          parentResource.setResourceHash(hash(parentResource, objectMapper));
          parentResource.setLabel(EMPTY);
          instance.getOutgoingEdges().add(new ResourceEdge(instance, parentResource,
            valueOf(fieldRule.getParentPredicate())));
        }
        appendEdge(parentResource, dataField, fieldRule);
      }
    }
  }

  private Resource selectParent(Resource resource, String parent) {
    if (resource.getTypes().stream().anyMatch(t -> t.name().equals(parent))) {
      return resource;
    }
    return resource.getOutgoingEdges().stream()
      .map(re -> selectParent(re.getTarget(), parent))
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(null);
  }

  private void enrichInstance(Resource instance, DataField dataField, Marc2BibframeRules.FieldRule fieldRule) {
    var properties = isNull(instance.getDoc()) ? new HashMap<String, List<String>>()
      : objectMapper.convertValue(instance.getDoc(), Map.class);
    mapProperties(instance, dataField, fieldRule, properties);
  }

  private void appendEdge(Resource resource, DataField dataField, Marc2BibframeRules.FieldRule fieldRule) {
    var edgeResource = new Resource();
    fieldRule.getTypes().stream().map(ResourceTypeDictionary::valueOf).forEach(edgeResource::addType);
    mapProperties(edgeResource, dataField, fieldRule, new HashMap<>());
    var labelFieldRule = fieldRule.getLabelField();
    if (nonNull(labelFieldRule)) {
      var labelField = dataField.getSubfield(labelFieldRule);
      if (nonNull(labelField)) {
        edgeResource.setLabel(labelField.getData().strip());
      }
    }
    if (isNull(edgeResource.getLabel())) {
      edgeResource.setLabel(EMPTY);
    }
    edgeResource.setResourceHash(hash(edgeResource, objectMapper));
    resource.getOutgoingEdges().add(new ResourceEdge(resource, edgeResource,
      valueOf(fieldRule.getPredicate())));
  }

  private void mapProperties(Resource resource, DataField dataField, Marc2BibframeRules.FieldRule fieldRule,
                             Map<String, List<String>> properties) {
    boolean concatProperties = fieldRule.isConcatProperties();
    fieldRule.getSubfields().forEach((field, rule) -> {
      var subfield = dataField.getSubfield(field);
      if (nonNull(subfield)) {
        mapProperty(properties, rule, subfield.getData(), concatProperties);
      }
    });
    mapProperty(properties, fieldRule.getInd1(), String.valueOf(isNotEmptyIndicator(dataField.getIndicator1())
      ? dataField.getIndicator1() : ""), concatProperties);
    mapProperty(properties, fieldRule.getInd2(), String.valueOf(isNotEmptyIndicator(dataField.getIndicator2())
      ? dataField.getIndicator2() : ""), concatProperties);
    resource.setDoc(getJsonNode(properties));
  }

  private boolean isNotEmptyIndicator(char indicator) {
    return !Character.isSpaceChar(indicator) && indicator != Character.MIN_VALUE;
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
