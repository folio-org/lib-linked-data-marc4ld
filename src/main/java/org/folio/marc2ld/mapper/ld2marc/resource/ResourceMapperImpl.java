package org.folio.marc2ld.mapper.ld2marc.resource;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.folio.ld.dictionary.PropertyDictionary.valueOf;
import static org.folio.marc2ld.configuration.property.Marc2BibframeRules.FieldCondition;
import static org.folio.marc2ld.mapper.marc2ld.condition.ConditionCheckerImpl.NOT;
import static org.folio.marc2ld.mapper.marc2ld.condition.ConditionCheckerImpl.PRESENTED;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules.FieldRule;
import org.folio.marc2ld.model.Resource;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceMapperImpl implements ResourceMapper {

  private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();
  private static final String CONTROL_FIELD_PREFIX = "00";
  private final Marc2BibframeRules rules;

  @Override
  public void handleResource(Resource resource, PredicateDictionary predicate, Record marcRecord) {
    var resourceTypes = resource.getTypes().stream().map(ResourceTypeDictionary::name).collect(Collectors.toSet());
    rules.getFieldRules().forEach((frKey, frValue) -> frValue.stream()
      .filter(fr -> Objects.equals(fr.getTypes(), resourceTypes)
        && (isNull(predicate) || predicate.name().equals(fr.getPredicate())))
      .forEach(fr -> {
        VariableField field;
        if (frKey.startsWith(CONTROL_FIELD_PREFIX)) {
          field = getControlField(fr, frKey, resource.getDoc());
        } else {
          field = getDataField(fr, frKey, resource.getDoc());
        }
        marcRecord.addVariableField(field);
      }));
    resource.getOutgoingEdges().forEach(re -> handleResource(re.getTarget(), re.getPredicate(), marcRecord));
  }

  private DataField getDataField(FieldRule fr, String tag, JsonNode doc) {
    var ind1 = getIndicator(fr.getInd1(), ofNullable(fr.getCondition()).map(FieldCondition::getInd1).orElse(null), doc);
    var ind2 = getIndicator(fr.getInd2(), ofNullable(fr.getCondition()).map(FieldCondition::getInd2).orElse(null), doc);
    var field = MARC_FACTORY.newDataField(tag, ind1, ind2);
    fr.getSubfields().forEach(
      (sfKey, sfValue) -> {
        var propertyUri = valueOf(sfValue).getValue();
        var jsonNode = doc.get(propertyUri);
        if (nonNull(jsonNode) && !jsonNode.isEmpty()) {
          jsonNode.elements().forEachRemaining(
            e -> field.addSubfield(MARC_FACTORY.newSubfield(sfKey, e.asText()))
          );
        }
      }
    );
    return field;
  }

  private char getIndicator(String indProperty, String indCondition, JsonNode doc) {
    return getIndicatorProperty(indProperty, doc)
      .or(() -> getIndicatorCondition(indCondition))
      .orElse(SPACE.charAt(0));
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
      .filter(ic -> isNotBlank(ic) && !ic.startsWith(NOT) && !ic.equals(PRESENTED))
      .map(c -> indCondition.charAt(0));
  }

  private ControlField getControlField(FieldRule fr, String tag, JsonNode doc) {
    var controlField = MARC_FACTORY.newControlField(tag);
    //tbd
    controlField.setData("cfDataTbd");
    return controlField;
  }

}
