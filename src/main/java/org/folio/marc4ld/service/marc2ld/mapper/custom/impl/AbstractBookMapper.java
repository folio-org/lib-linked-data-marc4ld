package org.folio.marc4ld.service.marc2ld.mapper.custom.impl;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.TERM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.folio.marc4ld.util.BibframeUtil.getWork;
import static org.folio.marc4ld.util.Constants.TAG_008;

import java.util.Arrays;
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

  protected abstract String getCategorySetLink();

  protected abstract String getCategorySetLabel();

  protected abstract String getLinkSuffix(char code);

  protected abstract String getTerm(char code);

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
        .forEach(c -> work.addOutgoingEdge(new ResourceEdge(work, createCategory((char) c), getPredicate()))));
  }

  private String getCharacterRange(Record marcRecord) {
    return marcRecord.getControlFields()
      .stream()
      .filter(controlField -> TAG_008.equals(controlField.getTag()))
      .map(controlField -> controlField.getData().substring(getStartIndex(), getEndIndex()))
      .findFirst()
      .orElse(EMPTY);
  }

  protected Resource createCategory(char code) {
    var categorySet = createResource(CATEGORY_SET, Map.of(
      LINK.getValue(), List.of(getCategorySetLink()),
      LABEL.getValue(), List.of(getCategorySetLabel())));
    return createResource(CATEGORY, Map.of(
      CODE.getValue(), List.of("" + code),
      LINK.getValue(), List.of(getCategorySetLink() + "/" + getLinkSuffix(code)),
      TERM.getValue(), List.of(getTerm(code))
    ), categorySet);
  }

  private Resource createResource(ResourceTypeDictionary type, Map<String, List<String>> properties,
                                  Resource... outgoingResources) {
    var resource = new Resource()
      .addType(type)
      .setDoc(mapperHelper.getJsonNode(properties));
    Arrays.stream(outgoingResources)
      .forEach(or -> resource.addOutgoingEdge(new ResourceEdge(resource, or, IS_DEFINED_BY)));
    labelService.setLabel(resource, properties);
    resource.setId(hashService.hash(resource));
    return resource;
  }
}
