package org.folio.marc4ld.service.marc2ld.field;

import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.condition.ConditionChecker;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.Marc2ldFieldRuleApplier;
import org.folio.marc4ld.service.marc2ld.mapper.AdditionalMapper;
import org.folio.marc4ld.service.marc2ld.mapper.AdditionalMapperController;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.folio.marc4ld.service.marc2ld.relation.RelationProvider;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceProcessorImpl implements ResourceProcessor {

  private final RelationProvider relationProvider;
  private final AdditionalMapperController additionalMapperController;
  private final FingerprintHashService hashService;
  private final MapperHelper mapperHelper;
  private final ConditionChecker conditionChecker;
  private final LabelService labelService;

  @Override
  public Collection<Resource> create(DataField dataField,
                                     List<ControlField> controlFields,
                                     Marc2ldFieldRuleApplier rule) {
    var fieldMapper = getMapper(dataField, controlFields, rule);
    var mappedResources = fieldMapper.createResources();
    mappedResources
      .forEach(resource -> additionalMapping(resource, rule, dataField, controlFields));
    return mappedResources;
  }

  @Override
  public void handleField(Resource parent,
                          DataField dataField,
                          List<ControlField> controlFields,
                          Marc2ldFieldRuleApplier rule) {
    var fieldMapper = getMapper(dataField, controlFields, rule);
    var mappedResources = fieldMapper.createResources(parent);

    mappedResources
      .forEach(resource -> additionalMapping(resource, rule, dataField, controlFields));
  }

  private void additionalMapping(Resource resource,
                                 Marc2ldFieldRuleApplier fieldRule,
                                 DataField dataField,
                                 List<ControlField> controlFields) {
    fieldRule.getEdgeRules()
      .stream()
      .filter(rule -> conditionChecker.isMarc2LdConditionSatisfied(rule.getOriginal(), dataField, controlFields))
      .forEach(rule -> handleField(resource, dataField, controlFields, rule));

    Optional.of(additionalMapperController.findAll(dataField.getTag()))
      .filter(CollectionUtils::isNotEmpty)
      .orElseGet(() -> additionalMapperController.findAll(controlFields))
      .stream()
      .filter(mapper -> isMapping(fieldRule, mapper))
      .findFirst()
      .ifPresent(m -> m.map(new MarcData(dataField, controlFields), resource));

    if (!resource.getTypes().contains(WORK)) {
      setLabel(resource);
    }
    resource.setId(hashService.hash(resource));
  }

  private void setLabel(Resource resource) {
    var properties = mapperHelper.getProperties(resource);
    labelService.setLabel(resource, properties);
    resource.setDoc(mapperHelper.getJsonNode(properties));
  }

  private static boolean isMapping(Marc2ldFieldRuleApplier fieldRule, AdditionalMapper mapper) {
    return Optional.of(fieldRule)
      .map(Marc2ldFieldRuleApplier::getPredicate)
      .map(mapper::canMap)
      .orElse(false);
  }

  private FieldMapper getMapper(DataField field, Collection<ControlField> controlFields, Marc2ldFieldRuleApplier rule) {
    return FieldMapperImpl.builder()
      .relationProvider(relationProvider)
      .mapperHelper(mapperHelper)
      .hashService(hashService)
      .labelService(labelService)
      .dataField(field)
      .controlFields(controlFields)
      .fieldRule(rule)
      .build();
  }
}
