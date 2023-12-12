package org.folio.marc2ld.mapper.ld2marc;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc2ld.util.BibframeUtil.isNotEmptyResource;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.model.Resource;
import org.marc4j.MarcJsonWriter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class Bibframe2MarcMapperImpl implements Bibframe2MarcMapper {

  private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();
  private final Marc2BibframeRules rules;

  @Override
  public String map(Resource bibframe) {
    if (!isNotEmptyResource(bibframe)) {
      log.warn("Given bibframe resource is empty, there is no doc and edges [{}]", bibframe);
      return null;
    }
    if (!bibframe.getTypes().equals(Set.of(INSTANCE))) {
      log.warn("Given bibframe resource is not an Instance [{}]", bibframe);
      return null;
    }
    try (var os = new ByteArrayOutputStream()) {
      var writer = new MarcJsonWriter(os);
      var marcRecord = MARC_FACTORY.newRecord();
      handleResource(bibframe, null, marcRecord);
      setLeader(marcRecord);
      writer.write(marcRecord);
      writer.close();
      return os.toString();
    } catch (IOException e) {
      log.error("Exception during bibframe to marc conversion", e);
      return null;
    }
  }

  private void handleResource(Resource resource, PredicateDictionary predicate, Record marcRecord) {
    var resourceTypes = resource.getTypes().stream().map(ResourceTypeDictionary::name).collect(Collectors.toSet());
    rules.getFieldRules().forEach((frKey, frValue) -> frValue.stream()
      .filter(fr -> Objects.equals(fr.getTypes(), resourceTypes)
        && (isNull(predicate) || predicate.name().equals(fr.getPredicate())))
      .forEach(fr -> {
        VariableField field;
        if (frKey.startsWith("00")) {
          field = getControlField(fr, frKey, resource.getDoc());
        } else {
          field = getDataField(fr, frKey, resource.getDoc());
        }
        marcRecord.addVariableField(field);
      }));
    resource.getOutgoingEdges().forEach(re -> handleResource(re.getTarget(), re.getPredicate(), marcRecord));
  }

  private DataField getDataField(Marc2BibframeRules.FieldRule fr, String tag, JsonNode doc) {
    var ind1 = getIndicator(fr.getInd1(), nonNull(fr.getCondition()) ? fr.getCondition().getInd1() : null, doc);
    var ind2 = getIndicator(fr.getInd2(), nonNull(fr.getCondition()) ? fr.getCondition().getInd2() : null, doc);
    var field = MARC_FACTORY.newDataField(tag, ind1, ind2);
    fr.getSubfields().forEach(
      (sfKey, sfValue) -> {
        var propertyUri = PropertyDictionary.valueOf(sfValue).getValue();
        var propertyNode = doc.get(propertyUri);
        if (nonNull(propertyNode) && !propertyNode.isEmpty()) {
          propertyNode.elements().forEachRemaining(
            e -> field.addSubfield(MARC_FACTORY.newSubfield(sfKey, e.asText()))
          );
        }
      }
    );
    return field;
  }

  private char getIndicator(String indProperty, String indCondition, JsonNode doc) {
    if (StringUtils.isNotEmpty(indProperty) && nonNull(doc.get(PropertyDictionary.valueOf(indProperty).getValue()))) {
      return doc.get(PropertyDictionary.valueOf(indProperty).getValue()).get(0).asText().charAt(0);
    }
    if (StringUtils.isNotBlank(indCondition)) {
      // tbd different indicator conditions
      return indCondition.charAt(0);
    }
    return Character.MIN_VALUE;
  }

  private ControlField getControlField(Marc2BibframeRules.FieldRule fr, String tag, JsonNode doc) {
    var controlField = MARC_FACTORY.newControlField(tag);
    //tbd
    controlField.setData("cfDataTbd");
    return controlField;
  }

  private void setLeader(Record marcRecord) {
    var leader = MARC_FACTORY.newLeader();
    // tbd
    leader.setRecordStatus('n'); // new
    leader.setTypeOfRecord('a'); // book record
    leader.setImplDefined1(new char[] {});
    leader.setCharCodingScheme('a');  // unicode
    leader.setIndicatorCount(2);
    leader.setSubfieldCodeLength(1);
    leader.setBaseAddressOfData(0);
    leader.setImplDefined2(new char[] {});
    leader.setEntryMap(new char[] {});
    marcRecord.setLeader(leader);
  }
}
