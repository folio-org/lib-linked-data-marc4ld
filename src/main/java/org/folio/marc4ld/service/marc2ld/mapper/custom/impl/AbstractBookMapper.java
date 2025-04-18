package org.folio.marc4ld.service.marc2ld.mapper.custom.impl;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.folio.marc4ld.util.Constants.TAG_008;
import static org.folio.marc4ld.util.LdUtil.getWork;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.custom.CustomMapper;
import org.folio.marc4ld.service.marc2ld.mapper.mapper.MapperHelper;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.Record;

@RequiredArgsConstructor
public abstract class AbstractBookMapper implements CustomMapper {

  private static final Set<Character> APPLICABLE_TYPES = Set.of('a', 't');
  private static final Set<Character> APPLICABLE_LEVELS = Set.of('a', 'c', 'd', 'm');

  private final LabelService labelService;
  private final MapperHelper mapperHelper;
  private final FingerprintHashService hashService;

  protected abstract int getStartIndex();

  protected abstract int getEndIndex();

  protected abstract boolean isSupportedCode(char code);

  protected abstract PredicateDictionary getPredicate();

  protected abstract void addSubResource(Resource resource, char code);

  @Override
  public boolean isApplicable(Record marcRecord) {
    return APPLICABLE_TYPES.contains(marcRecord.getLeader().getTypeOfRecord())
      && APPLICABLE_LEVELS.contains(marcRecord.getLeader().getImplDefined1()[0]);
  }

  @Override
  public void map(Record marcRecord, Resource instance) {
    getWork(instance)
      .ifPresent(work -> getCharacterRange(marcRecord)
        .chars()
        .filter(c -> isSupportedCode((char) c))
        .forEach(c -> addSubResource(work, (char) c)));
  }

  protected Resource createResource(Set<ResourceTypeDictionary> types, Map<String, List<String>> properties,
                                    Map<PredicateDictionary, Resource> outgoingResources) {
    var resource = new Resource()
      .setTypes(types)
      .setDoc(mapperHelper.getJsonNode(properties));
    outgoingResources.forEach((key, value) -> resource.addOutgoingEdge(new ResourceEdge(resource, value, key)));
    labelService.setLabel(resource, properties);
    resource.setId(hashService.hash(resource));
    return resource;
  }

  private String getCharacterRange(Record marcRecord) {
    return marcRecord.getControlFields()
      .stream()
      .filter(controlField -> TAG_008.equals(controlField.getTag()))
      .map(ControlField::getData)
      .filter(data -> data.length() >= getEndIndex())
      .map(data -> data.substring(getStartIndex(), getEndIndex()))
      .findFirst()
      .orElse(EMPTY);
  }
}
