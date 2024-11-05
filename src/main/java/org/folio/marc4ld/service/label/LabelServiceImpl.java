package org.folio.marc4ld.service.label;

import static org.folio.ld.dictionary.PropertyDictionary.LABEL;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.service.label.processor.LabelProcessor;
import org.folio.marc4ld.service.label.processor.UuidLabelProcessor;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class LabelServiceImpl implements LabelService {

  private final Map<Set<ResourceTypeDictionary>, LabelController> typesControllers;
  private final LabelProcessor defaultProcessor;
  private final LabelController defaultController;

  public LabelServiceImpl(Marc4BibframeRules rules, LabelProcessorFactory labelProcessorFactory) {
    this.defaultProcessor = new UuidLabelProcessor();
    this.defaultController = new LabelController(List.of(defaultProcessor), false);

    this.typesControllers = rules.getLabelRules()
      .stream()
      .collect(Collectors.toMap(this::getTypes,
        rule -> new LabelController(labelProcessorFactory.get(rule), rule.isAddLabelProperty())));
  }

  @Override
  public void setLabel(Resource resource, Map<String, List<String>> properties) {
    if (properties.isEmpty()) {
      return;
    }
    var controller = getController(resource);
    var label = controller.labelProcessors
      .stream()
      .map(p -> p.apply(properties))
      .filter(StringUtils::isNotBlank)
      .findFirst()
      .orElseGet(() -> {
        log.warn("No label configuration for types: {}. Generating default label.", resource.getTypes());
        return defaultProcessor.apply(properties);
      });
    resource.setLabel(label);
    if (controller.addLabelProperty) {
      properties.put(LABEL.getValue(), List.of(label));
    }
  }

  private LabelController getController(Resource resource) {
    return Optional.of(resource)
      .map(Resource::getTypes)
      .filter(CollectionUtils::isNotEmpty)
      .map(typesControllers::get)
      .orElse(defaultController);
  }

  private Set<ResourceTypeDictionary> getTypes(Marc4BibframeRules.LabelRule rule) {
    return rule.getTypes()
      .stream()
      .map(ResourceTypeDictionary::valueOf)
      .collect(Collectors.toSet());
  }

  private record LabelController(Collection<LabelProcessor> labelProcessors, boolean addLabelProperty) {
  }
}
