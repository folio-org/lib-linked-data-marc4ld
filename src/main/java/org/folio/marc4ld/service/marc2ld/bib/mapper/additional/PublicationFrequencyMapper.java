package org.folio.marc4ld.service.marc2ld.bib.mapper.additional;

import static java.util.Optional.ofNullable;
import static org.folio.ld.dictionary.PredicateDictionary.PUBLICATION_FREQUENCY;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.marc4ld.util.Constants.Dictionary.PUBLICATION_FREQUENCY_CODE_TO_LABEL;
import static org.folio.marc4ld.util.Constants.TAG_008;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.specific.PublicationFrequencyDictionary;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.springframework.stereotype.Component;

@Component
public class PublicationFrequencyMapper extends DictionaryBasedMarc2LdMapper {

  private static final int APPLICABLE_008_POSITION = 18;
  private static final List<String> TAGS = List.of(TAG_008);
  private static final MappingConfig CONFIG_LABEL = new MappingConfig(PUBLICATION_FREQUENCY_CODE_TO_LABEL, CODE, LABEL);

  PublicationFrequencyMapper(DictionaryProcessor dictionaryProcessor, MapperHelper mapperHelper) {
    super(dictionaryProcessor, mapperHelper);
  }

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  @Override
  public boolean canMap(Marc4LdRules.FieldRule fieldRule) {
    return PUBLICATION_FREQUENCY.name().equals(fieldRule.getPredicate())
            && isApplicable008Position(fieldRule);
  }

  private boolean isApplicable008Position(Marc4LdRules.FieldRule fieldRule) {
    return ofNullable(fieldRule.getControlFields())
            .map(cfs -> cfs.get(TAG_008))
            .map(cf -> cf.get(CODE.name()))
            .map(List::getFirst)
            .map(v ->  v.equals(APPLICABLE_008_POSITION))
            .orElse(false);
  }

  @Override
  Set<MappingConfig> getMappingConfigs() {
    return Set.of(CONFIG_LABEL);
  }

  @Override
  public void map(MarcData marcData, Resource resource) {
    super.map(marcData, resource);
    mapLinks(resource);
  }

  private void mapLinks(Resource resource) {
    var properties = mapperHelper.getProperties(resource);
    var codes = properties.getOrDefault(CODE.getValue(), List.of());
    var links = codes.stream()
      .map(code -> code.charAt(0))
      .map(PublicationFrequencyDictionary::getValue)
      .flatMap(Optional::stream)
      .toList();
    if (links.isEmpty()) {
      return;
    }
    properties.put(LINK.getValue(), links);
    resource.setDoc(mapperHelper.getJsonNode(properties));
  }
}
