package org.folio.marc4ld.service.marc2ld.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.marc4j.marc.ControlField;
import org.springframework.stereotype.Component;

@Component
public class Marc2ldMapperControllerImpl implements Marc2ldMapperController {

  private final Map<String, List<Marc2ldMapper>> marc2ldMappersMap;

  public Marc2ldMapperControllerImpl(Collection<Marc2ldMapper> marc2LdMappers) {
    this.marc2ldMappersMap = marc2LdMappers.stream()
      .collect(Collectors.groupingBy(Marc2ldMapper::getTag));
  }

  @Override
  public Collection<Marc2ldMapper> findAll(String tag) {
    return marc2ldMappersMap.getOrDefault(tag, Collections.emptyList());
  }

  @Override
  public Collection<Marc2ldMapper> findAll(List<ControlField> controlFields) {
    return controlFields.stream()
      .map(ControlField::getTag)
      .map(this::findAll)
      .flatMap(Collection::stream)
      .toList();
  }
}
