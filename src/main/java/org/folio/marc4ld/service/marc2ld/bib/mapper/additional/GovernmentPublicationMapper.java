package org.folio.marc4ld.service.marc2ld.bib.mapper.additional;

import static org.folio.ld.dictionary.PredicateDictionary.GOVERNMENT_PUBLICATION;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.TERM;
import static org.folio.marc4ld.util.Constants.TAG_008;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.marc2ld.mapper.AdditionalMapper;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.marc4j.marc.ControlField;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GovernmentPublicationMapper implements AdditionalMapper {
  private static final int GOVT_PUB_CHAR_INDEX = 28;
  private static final List<String> TAGS = List.of(TAG_008);

  private static final Map<Character, String> MARC_CODE_TO_TERM_MAP = Map.of(
    'a', "Autonomous",
    'c', "Multilocal",
    'f', "Federal",
    'i', "International Intergovernmental",
    'l', "Local",
    'm', "Multistate",
    'o', "Government",
    's', "State"
  );
  private static final String LINK_PREFIX = "http://id.loc.gov/vocabulary/mgovtpubtype/";
  // Marc code 'o' is mapped to 'g' in the id.loc.gov
  private static final Function<Character, String> LINK_MAPPER = code -> LINK_PREFIX + (code == 'o' ? 'g' : code);

  private final MapperHelper mapperHelper;

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  @Override
  public boolean canMap(PredicateDictionary predicate) {
    return predicate == GOVERNMENT_PUBLICATION;
  }

  @Override
  public void map(MarcData marcData, Resource resource) {
    mapperHelper.getControlField(marcData.getControlFields(), TAG_008, GOVT_PUB_CHAR_INDEX + 1)
      .ifPresent(cf -> processControlField(resource, cf));
  }

  private void processControlField(Resource resource, ControlField controlField) {
    var code = controlField.getData().charAt(GOVT_PUB_CHAR_INDEX);
    var link = LINK_MAPPER.apply(code);
    var term = MARC_CODE_TO_TERM_MAP.get(code);
    mapperHelper.addPropertiesToResource(
      resource,
      Map.of(
        LINK.getValue(), List.of(link),
        TERM.getValue(), List.of(term)
      )
    );
    resource.setLabel(term);
  }
}
