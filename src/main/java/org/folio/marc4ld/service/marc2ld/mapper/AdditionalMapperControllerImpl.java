package org.folio.marc4ld.service.marc2ld.mapper;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.marc4j.marc.ControlField;
import org.springframework.stereotype.Component;

@Component
public class AdditionalMapperControllerImpl implements AdditionalMapperController {

  private final Map<String, List<AdditionalMapper>> marc2ldMappersMap;

  public AdditionalMapperControllerImpl(Collection<AdditionalMapper> additionalMappers) {
    marc2ldMappersMap = getMarc2LdMappersMap(additionalMappers);
  }

  @Override
  public Collection<AdditionalMapper> findAll(String tag) {
    return marc2ldMappersMap.getOrDefault(tag, Collections.emptyList());
  }

  @Override
  public Collection<AdditionalMapper> findAll(List<ControlField> controlFields) {
    return controlFields.stream()
      .map(ControlField::getTag)
      .map(this::findAll)
      .flatMap(Collection::stream)
      .toList();
  }

  private Map<String, List<AdditionalMapper>> getMarc2LdMappersMap(Collection<AdditionalMapper> additionalMappers) {
    record TagAndMapper(String tag, AdditionalMapper mapper) {
    }

    return additionalMappers.stream()
      .flatMap(mapper -> mapper.getTags().stream().map(tag -> new TagAndMapper(tag, mapper)))
      .collect(groupingBy(TagAndMapper::tag, mapping(TagAndMapper::mapper, toList())));
  }
}
