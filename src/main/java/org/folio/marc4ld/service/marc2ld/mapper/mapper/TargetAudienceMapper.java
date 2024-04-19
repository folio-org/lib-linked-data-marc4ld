package org.folio.marc4ld.service.marc2ld.mapper.mapper;

import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.PredicateDictionary.TARGET_AUDIENCE;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.TERM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.marc2ld.mapper.Marc2ldMapper;
import org.marc4j.marc.ControlField;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TargetAudienceMapper implements Marc2ldMapper {

  private static final String TAG = "008";
  private static final int TARGET_AUD_CHAR_INDEX = 22;
  private static final Map<Character, String> MARC_CODE_TO_LINK_SUFFIX_MAP = Map.of(
    'a', "pre",
    'b', "pri",
    'c', "pad",
    'd', "ado",
    'e', "adu",
    'f', "spe",
    'g', "gen",
    'j', "juv"
  );
  private static final Map<Character, String> MARC_CODE_TO_TERM_MAP = Map.of(
    'a', "Preschool",
    'b', "Primary",
    'c', "Pre-adolescent",
    'd', "Adolescent",
    'e', "Adult",
    'f', "Specialized",
    'g', "General",
    'j', "Juvenile"
  );
  private static final String LINK_PREFIX = "http://id.loc.gov/vocabulary/maudience/";
  private static final Set<ResourceTypeDictionary> CATEGORY_SET_TYPES = Set.of(CATEGORY_SET);
  private static final List<String> CATEGORY_SET_LINK = List.of("https://id.loc.gov/vocabulary/maudience");
  private static final List<String> CATEGORY_SET_LABEL = List.of("Target audience");

  private final FingerprintHashService hashService;
  private final MapperHelper mapperHelper;

  @Override
  public String getTag() {
    return TAG;
  }

  @Override
  public boolean canMap(PredicateDictionary predicate) {
    return predicate == TARGET_AUDIENCE;
  }

  @Override
  public void map(MarcData marcData, Resource resource) {
    mapperHelper.getControlField(marcData.getControlFields(), TAG, TARGET_AUD_CHAR_INDEX + 1)
      .ifPresent(controlField -> processControlField(resource, controlField));
  }

  private void processControlField(Resource resource, ControlField controlField) {
    var code = controlField.getData().charAt(TARGET_AUD_CHAR_INDEX);
    var link = LINK_PREFIX + MARC_CODE_TO_LINK_SUFFIX_MAP.get(code);
    var term = MARC_CODE_TO_TERM_MAP.get(code);
    mapperHelper.addPropertiesToResource(
      resource,
      Map.of(
        LINK.getValue(), List.of(link),
        TERM.getValue(), List.of(term)
      )
    );
    resource.setLabel(term);
    resource.getOutgoingEdges().add(new ResourceEdge(resource, getCategorySet(), IS_DEFINED_BY));
  }

  private Resource getCategorySet() {
    var categorySet = new Resource()
      .setTypes(CATEGORY_SET_TYPES)
      .setLabel(CATEGORY_SET_LABEL.get(0));
    mapperHelper.addPropertiesToResource(
      categorySet,
      Map.of(
        LINK.getValue(), CATEGORY_SET_LINK,
        LABEL.getValue(), CATEGORY_SET_LABEL
      )
    );
    categorySet.setId(hashService.hash(categorySet));
    return categorySet;
  }
}
