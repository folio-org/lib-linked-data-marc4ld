package org.folio.marc4ld.service.ld2marc.mapper.impl.agent;

import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PropertyDictionary.AFFILIATION;
import static org.folio.ld.dictionary.PropertyDictionary.AUTHORITY_LINK;
import static org.folio.ld.dictionary.PropertyDictionary.CONTROL_FIELD;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.EQUIVALENT;
import static org.folio.ld.dictionary.PropertyDictionary.FIELD_LINK;
import static org.folio.ld.dictionary.PropertyDictionary.LINKAGE;
import static org.folio.ld.dictionary.PropertyDictionary.MISC_INFO;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.NUMBER_OF_PARTS;
import static org.folio.ld.dictionary.PropertyDictionary.PLACE;
import static org.folio.ld.dictionary.PropertyDictionary.SUBORDINATE_UNIT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.JURISDICTION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.marc4ld.util.Constants.ONE;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.TAG_110;
import static org.folio.marc4ld.util.Constants.TAG_710;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
public class OrganizationJurisdictionMapper extends AgentMapper {

  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(ORGANIZATION, JURISDICTION);
  private static final Map<Character, String> REPEATABLE_SUBFIELD_PROPERTY_MAP = Map.of(
    'b', SUBORDINATE_UNIT.getValue(),
    'c', PLACE.getValue(),
    'd', DATE.getValue(),
    'g', MISC_INFO.getValue(),
    'n', NUMBER_OF_PARTS.getValue(),
    '0', AUTHORITY_LINK.getValue(),
    '1', EQUIVALENT.getValue(),
    '7', CONTROL_FIELD.getValue(),
    '8', FIELD_LINK.getValue()
  );
  private static final Map<Character, String> NON_REPEATABLE_SUBFIELD_PROPERTY_MAP = Map.of(
    'a', NAME.getValue(),
    'u', AFFILIATION.getValue(),
    '6', LINKAGE.getValue()
  );

  public OrganizationJurisdictionMapper(DictionaryProcessor dictionaryProcessor,
                                        MarcFactory marcFactory, Comparator<Subfield> comparator) {
    super(dictionaryProcessor, marcFactory, comparator);
  }

  @Override
  protected Set<ResourceTypeDictionary> getSupportedTypes() {
    return SUPPORTED_TYPES;
  }

  @Override
  protected String getTag(ResourceEdge resourceEdge) {
    return resourceEdge.getPredicate() == CREATOR ? TAG_110 : TAG_710;
  }

  @Override
  protected Map<Character, String> getRepeatableSubfieldPropertyMap() {
    return REPEATABLE_SUBFIELD_PROPERTY_MAP;
  }

  @Override
  protected Map<Character, String> getNonRepeatableSubfieldPropertyMap() {
    return NON_REPEATABLE_SUBFIELD_PROPERTY_MAP;
  }

  @Override
  protected char getIndicator1(Resource resource) {
    return resource.getTypes().contains(JURISDICTION) ? ONE : SPACE;
  }
}
