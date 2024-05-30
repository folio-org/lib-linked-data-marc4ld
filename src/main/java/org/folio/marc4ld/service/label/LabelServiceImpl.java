package org.folio.marc4ld.service.label;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.service.label.processor.LabelProcessor;
import org.folio.marc4ld.service.label.processor.UuidLabelProcessor;
import org.springframework.stereotype.Service;

@Service
public class LabelServiceImpl implements LabelService {

  private final Map<Set<ResourceTypeDictionary>, Collection<LabelProcessor>> typesProcessors;
  private final LabelProcessor defaultProcessor = new UuidLabelProcessor();

  public LabelServiceImpl(Marc4BibframeRules rules, LabelProcessorFactory labelProcessorFactory) {
    this.typesProcessors = rules.getLabelRules()
      .stream()
      .collect(Collectors.toMap(this::getTypes, labelProcessorFactory::get));
  }

  @Override
  public void setLabel(Resource resource, Map<String, List<String>> properties) {
    var label = getProcessors(resource)
      .stream()
      .map(p -> p.apply(properties))
      .filter(StringUtils::isNotBlank)
      .findFirst()
      .orElseGet(() -> defaultProcessor.apply(properties));
    resource.setLabel(label);
  }

  private Collection<LabelProcessor> getProcessors(Resource resource) {
    return Optional.of(resource)
      .map(Resource::getTypes)
      .filter(CollectionUtils::isNotEmpty)
      .map(typesProcessors::get)
      .orElseGet(Collections::emptyList);
  }

  private Set<ResourceTypeDictionary> getTypes(Marc4BibframeRules.LabelRule rule) {
    return rule.getTypes()
      .stream()
      .map(ResourceTypeDictionary::valueOf)
      .collect(Collectors.toSet());
  }
}
