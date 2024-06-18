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
public class Marc2ldMapperControllerImpl implements Marc2ldMapperController {

  private final Map<String, List<Marc2ldMapper>> marc2ldMappersMap;

  public Marc2ldMapperControllerImpl(Collection<Marc2ldMapper> marc2LdMappers) {
    marc2ldMappersMap = getMarc2LdMappersMap(marc2LdMappers);
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

  private Map<String, List<Marc2ldMapper>> getMarc2LdMappersMap(Collection<Marc2ldMapper> marc2LdMappers) {
    record TagAndMapper(String tag, Marc2ldMapper mapper) {
    }

    return marc2LdMappers.stream()
      .flatMap(mapper -> mapper.getTags().stream().map(tag -> new TagAndMapper(tag, mapper)))
      .collect(groupingBy(TagAndMapper::tag, mapping(TagAndMapper::mapper, toList())));
  }
}
