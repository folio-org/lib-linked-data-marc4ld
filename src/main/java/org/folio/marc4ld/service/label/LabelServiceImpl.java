package org.folio.marc4ld.service.label;

import static org.folio.ld.dictionary.PropertyDictionary.LABEL;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.label.LabelGeneratorService;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.service.label.processor.LabelProcessor;
import org.folio.marc4ld.service.label.processor.UuidLabelProcessor;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class LabelServiceImpl implements LabelService {

  private final LabelGeneratorService labelGeneratorService;
  private final Map<Set<ResourceTypeDictionary>, LabelController> typesControllers;
  private final LabelProcessor defaultProcessor;
  private final LabelController defaultController;

  public LabelServiceImpl(LabelGeneratorService labelGeneratorService, Marc4LdRules rules,
                          LabelProcessorFactory labelProcessorFactory) {
    this.labelGeneratorService = labelGeneratorService;
    this.defaultProcessor = new UuidLabelProcessor();
    this.defaultController = new LabelController(List.of(defaultProcessor), false);

    this.typesControllers = rules.getLabelRules()
      .stream()
      .collect(Collectors.toMap(this::getTypes,
        rule -> new LabelController(labelProcessorFactory.get(rule), rule.isAddLabelProperty())));
  }

  @Override
  public void setLabel(Resource resource, Map<String, List<String>> properties) {
    var controller = getController(resource);
    var generatedLabel = labelGeneratorService.getLabel(resource);
    var label = shouldUseDeprecatedLabel(generatedLabel, resource.getLabel())
      ? getDeprecatedLabel(resource, properties, controller)
      : generatedLabel;
    if (Objects.isNull(label)) {
      return;
    }
    resource.setLabel(label);
    if (controller.addLabelProperty) {
      properties.put(LABEL.getValue(), List.of(label));
    }
  }

  private boolean shouldUseDeprecatedLabel(String generatedLabel, String currentLabel) {
    return StringUtils.isBlank(generatedLabel) || Objects.equals(generatedLabel, currentLabel);
  }

  private String getDeprecatedLabel(Resource resource,
                                    Map<String, List<String>> properties,
                                    LabelController controller) {
    if (properties.isEmpty()) {
      return null;
    }
    return controller.labelProcessors
      .stream()
      .map(p -> p.apply(properties))
      .filter(StringUtils::isNotBlank)
      .findFirst()
      .orElseGet(() -> {
        log.warn("No label configuration for types: {}. Generating default label.", resource.getTypes());
        return defaultProcessor.apply(properties);
      });
  }

  private LabelController getController(Resource resource) {
    return Optional.of(resource)
      .map(Resource::getTypes)
      .filter(CollectionUtils::isNotEmpty)
      .map(typesControllers::get)
      .orElse(defaultController);
  }

  private Set<ResourceTypeDictionary> getTypes(Marc4LdRules.LabelRule rule) {
    return rule.getTypes()
      .stream()
      .map(ResourceTypeDictionary::valueOf)
      .collect(Collectors.toSet());
  }

  @Deprecated(forRemoval = true)
  private record LabelController(Collection<LabelProcessor> labelProcessors, boolean addLabelProperty) {
  }
}
