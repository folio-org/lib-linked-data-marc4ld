package org.folio.marc4ld.service.ld2marc.resource;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.folio.ld.dictionary.PropertyDictionary.valueOf;
import static org.folio.marc4ld.configuration.property.Marc4BibframeRules.Marc2ldCondition;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;
import static org.folio.marc4ld.util.Constants.SUBFIELD_SRS_ID;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules.FieldRule;
import org.folio.marc4ld.model.Resource;
import org.folio.marc4ld.service.condition.ConditionChecker;
import org.folio.marc4ld.service.condition.ConditionCheckerImpl;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.ld2marc.resource.field.ControlFieldsBuilder;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Resource2MarcRecordMapperImpl implements Resource2MarcRecordMapper {

  private static final String CONTROL_FIELD_PREFIX = "00";
  private final MarcFactory marcFactory;
  private final Marc4BibframeRules rules;
  private final ConditionChecker conditionChecker;
  private final DictionaryProcessor dictionaryProcessor;

  @Override
  public Record toMarcRecord(Resource resource) {
    var marcRecord = marcFactory.newRecord();
    var cfb = new ControlFieldsBuilder();
    var dataFields = getFields(resource, null, cfb);
    Stream.concat(cfb.build(marcFactory), dataFields.stream())
      .sorted(comparing(VariableField::getTag))
      .forEach(marcRecord::addVariableField);
    addInternalIds(marcRecord, resource);
    return marcRecord;
  }

  private List<DataField> getFields(Resource resource, PredicateDictionary predicate,
                                    ControlFieldsBuilder cfb) {
    var resourceTypes = resource.getTypes().stream().map(ResourceTypeDictionary::name).collect(Collectors.toSet());
    var dataFields = rules.getFieldRules().entrySet().stream()
      .flatMap(tagToRule -> tagToRule.getValue().stream()
        .flatMap(fr -> Stream.concat(Stream.of(fr), ofNullable(fr.getEdges()).orElse(emptyList()).stream()))
        .filter(fr -> Objects.equals(fr.getTypes(), resourceTypes)
          && (isNull(predicate) || predicate.name().equals(fr.getPredicate())))
        .map(fr -> {
          if (nonNull(fr.getControlFields())) {
            collectControlFields(cfb, fr.getControlFields(), resource.getDoc());
          }
          if (!tagToRule.getKey().startsWith(CONTROL_FIELD_PREFIX)) {
            return getDataField(fr, tagToRule.getKey(), resource);
          }
          return null;
        })
        .filter(Objects::nonNull));
    return Stream.concat(dataFields,
        resource.getOutgoingEdges().stream().flatMap(re -> getFields(re.getTarget(), re.getPredicate(), cfb).stream()))
      .toList();
  }

  private DataField getDataField(FieldRule fr, String tag, Resource resource) {
    if (isNull(fr.getSubfields())) {
      return null;
    }
    var doc = resource.getDoc();
    var ind1 = getIndicator(fr.getInd1(), getIndCondition(fr, Marc2ldCondition::getInd1), doc);
    var ind2 = getIndicator(fr.getInd2(), getIndCondition(fr, Marc2ldCondition::getInd2), doc);
    var field = marcFactory.newDataField(tag, ind1, ind2);
    fr.getSubfields().forEach(
      (sfKey, sfValue) -> {
        var propertyUri = valueOf(sfValue).getValue();
        var jsonNode = doc.get(propertyUri);
        if (nonNull(jsonNode) && !jsonNode.isEmpty()) {
          jsonNode.elements().forEachRemaining(
            e -> {
              if (field.getSubfields().stream().noneMatch(sf -> sf.getData().equals(e.asText()))) {
                field.addSubfield(marcFactory.newSubfield(sfKey, e.asText()));
              }
            }
          );
        }
      }
    );
    return conditionChecker.isLd2MarcConditionSatisfied(fr, resource) && !field.getSubfields().isEmpty() ? field : null;
  }

  private char getIndicator(String indProperty, String indCondition, JsonNode doc) {
    return getIndicatorProperty(indProperty, doc)
      .or(() -> getIndicatorCondition(indCondition))
      .orElse(SPACE.charAt(0));
  }

  private String getIndCondition(FieldRule fr, Function<Marc2ldCondition, String> indGetter) {
    return ofNullable(fr.getMarc2ldCondition()).map(indGetter).orElse(null);
  }

  private Optional<Character> getIndicatorProperty(String indProperty, JsonNode doc) {
    if (isNotEmpty(indProperty)) {
      var property = valueOf(indProperty).getValue();
      var jsonNode = doc.get(property);
      if (nonNull(jsonNode) && !jsonNode.isEmpty()) {
        return Optional.of(jsonNode.get(0).asText().charAt(0));
      }
    }
    return Optional.empty();
  }

  private Optional<Character> getIndicatorCondition(String indCondition) {
    return ofNullable(indCondition)
      .filter(ic -> isNotBlank(ic) && !ic.startsWith(ConditionCheckerImpl.NOT) && !ic.equals(
        ConditionCheckerImpl.PRESENTED))
      .map(c -> indCondition.charAt(0));
  }

  private void collectControlFields(ControlFieldsBuilder cfb, Map<String, Map<String, List<Integer>>> controlFieldsRule,
                                    JsonNode doc) {
    controlFieldsRule.forEach((tag, valueRules) -> valueRules.forEach((key, value) -> {
      var property = PropertyDictionary.valueOf(key).getValue();
      if (doc.has(property)) {
        var propertyValue = doc.get(property).get(0).asText();
        propertyValue = dictionaryProcessor.getKey(key, propertyValue).orElse(propertyValue);
        cfb.addFieldValue(tag, propertyValue, value.get(0), value.get(1));
      }
    }));
  }

  private void addInternalIds(Record marcRecord, Resource resource) {
    if (nonNull(resource.getInventoryId()) || nonNull(resource.getSrsId())) {
      var field999 = marcFactory.newDataField(FIELD_UUID, SPACE.charAt(0), SPACE.charAt(0));
      ofNullable(resource.getInventoryId()).ifPresent(
        id -> field999.addSubfield(marcFactory.newSubfield(SUBFIELD_INVENTORY_ID, id.toString())));
      ofNullable(resource.getSrsId()).ifPresent(
        id -> field999.addSubfield(marcFactory.newSubfield(SUBFIELD_SRS_ID, id.toString())));
      marcRecord.addVariableField(field999);
    }
  }

}
