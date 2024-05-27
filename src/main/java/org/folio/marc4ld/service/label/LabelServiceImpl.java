package org.folio.marc4ld.service.label;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.label.processor.LabelProcessor;
import org.folio.marc4ld.service.label.processor.UuidLabelProcessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

  private final Map<Set<ResourceTypeDictionary>, LabelProcessor> typesProcessors;
  private final LabelProcessor defaultProcessor = new UuidLabelProcessor();

  @Override
  public void setLabel(Resource resource, Map<String, List<String>> properties) {
    var processor = getProcessor(resource);
    var label = processor.apply(properties);
    resource.setLabel(label);
  }

  private LabelProcessor getProcessor(Resource resource) {
    return Optional.of(resource)
      .map(Resource::getTypes)
      .filter(CollectionUtils::isNotEmpty)
      .map(typesProcessors::get)
      .orElse(defaultProcessor);
  }
}
